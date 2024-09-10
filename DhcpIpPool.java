import java.util.HashSet;
import java.util.Set;

public class DhcpIpPool {
    private Set<String> availableIps;
    private Set<String> allocatedIps;

    public DhcpIpPool() {
        availableIps = new HashSet<>();
        allocatedIps = new HashSet<>();
        // Add your desired IP range to the availableIps set
        initializeIpRange("192.168.0.1", "192.168.0.254");
    }

    private void initializeIpRange(String startIp, String endIp) {
        // Convert start and end IP addresses to long values for easy comparison
        long start = ipToLong(startIp);
        long end = ipToLong(endIp);

        for (long ip = start; ip <= end; ip++) {
            availableIps.add(longToIp(ip));
        }
    }

    public synchronized String allocateIp() {
        if (availableIps.isEmpty()) {
            return null; // No available IP address
        }

        String ip = availableIps.iterator().next();
        availableIps.remove(ip);
        allocatedIps.add(ip);
        return ip;
    }

    public synchronized void releaseIp(String ip) {
        if (allocatedIps.contains(ip)) {
            allocatedIps.remove(ip);
            availableIps.add(ip);
        }
    }

    // Helper method to convert IP address string to a long value
    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + Integer.parseInt(parts[i]);
        }
        return result;
    }

    // Helper method to convert a long value to IP address string
    private String longToIp(long ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip >>= 8;
        }
        return sb.toString();
    }

    
}
