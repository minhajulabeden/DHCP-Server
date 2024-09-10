import java.nio.ByteBuffer;
import java.util.Arrays;
public class Converter {
    public static byte[] toBytes(int[] list) {
        int k = list.length;
		byte[] out = new byte[k];
		for(int i=0; i<list.length; i++){
			out[i] = (byte)list[i];
		}
		return out;
    }
}

