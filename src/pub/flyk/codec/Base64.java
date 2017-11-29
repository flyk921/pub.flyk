package pub.flyk.codec;

import java.util.Arrays;

import pub.flyk.utils.StringUtil;

public class Base64 {
	
	static class Context {
		
		/**
		 * Place holder for the bytes we're dealing with for our based logic.
		 * Bitwise operations store and extract the encoding or decoding from this variable.
		 */
		int ibitWorkArea;
		
		/**
		 * Place holder for the bytes we're dealing with for our based logic.
		 * Bitwise operations store and extract the encoding or decoding from this variable.
		 */
		long lbitWorkArea;
		
		/**
		 * Buffer for streaming.
		 */
		byte[] buffer;
		
		/**
		 * Position where next character should be written in the buffer.
		 */
		int pos;
		
		/**
		 * Position where next character should be read from the buffer.
		 */
		int readPos;
		
		/**
		 * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
		 * and must be thrown away.
		 */
		boolean eof;
		
		/**
		 * Variable tracks how many characters have been written to the current line. Only used when encoding. We use
		 * it to make sure each encoded line never goes beyond lineLength (if lineLength > 0).
		 */
		int currentLinePos;
		
		/**
		 * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding. This
		 * variable helps track that.
		 */
		int modulus;
		
		Context() {
		}
		
		/**
		 * Returns a String useful for debugging (especially within a debugger.)
		 *
		 * @return a String useful for debugging.
		 */
		@Override
		public String toString() {
			return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, " +
					"modulus=%s, pos=%s, readPos=%s]", this.getClass().getSimpleName(), Arrays.toString(buffer),
					currentLinePos, eof, ibitWorkArea, lbitWorkArea, modulus, pos, readPos);
		}
	}

	
	
	
	private final int unencodedBlockSize;
	private final int encodedBlockSize;
	private final int lineLength;
	private final int chunkSeparatorLength;
	
	private static final int EOF = -1;
	private static final int BITS_PER_ENCODED_BYTE = 6;
	private static final int BYTES_PER_UNENCODED_BLOCK = 3;
	private static final int BYTES_PER_ENCODED_BLOCK = 4;
	private static final byte PAD_DEFAULT = '='; 
	private final byte PAD = PAD_DEFAULT;
	private static final byte[] CHUNK_SEPARATOR = {'\r', '\n'};
	private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final int MASK_6BITS = 0x3f;
	private static final int MASK_8BITS = 0xff;
	
	private static final byte[] DECODE_TABLE = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
		55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
		5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
		24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
		35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
	};
	private final byte[] decodeTable = DECODE_TABLE;
	
	private static final byte[] STANDARD_ENCODE_TABLE = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};
	private final int encodeSize;
	private final byte[] lineSeparator;
	private final int decodeSize;
	private final byte[] encodeTable;
	
	private Base64(final int lineLength, final byte[] lineSeparator) {
        this.unencodedBlockSize = BYTES_PER_UNENCODED_BLOCK;
        this.encodedBlockSize = BYTES_PER_ENCODED_BLOCK;
        this.chunkSeparatorLength = lineSeparator == null ? 0 : lineSeparator.length;
        final boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking ? (lineLength / encodedBlockSize) * encodedBlockSize : 0;
        
        if (lineSeparator != null) {
            if (containsAlphabetOrPad(lineSeparator)) {
                final String sep = StringUtil.newStringUtf8(lineSeparator);
                throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
            }
            if (lineLength > 0){ 
                this.encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.length;
                this.lineSeparator = new byte[lineSeparator.length];
                System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
            } else {
                this.encodeSize = BYTES_PER_ENCODED_BLOCK;
                this.lineSeparator = null;
            }
        } else {
            this.encodeSize = BYTES_PER_ENCODED_BLOCK;
            this.lineSeparator = null;
        }
        this.decodeSize = this.encodeSize - 1;
        this.encodeTable = STANDARD_ENCODE_TABLE;
    }
	
	private boolean containsAlphabetOrPad(final byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        }
        for (final byte element : arrayOctet) {
            if (PAD == element || isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }
	
	private boolean isInAlphabet(final byte octet) {
		return octet >= 0 && octet < decodeTable.length && decodeTable[octet] != -1;
	}

	public static String encodeBase64(final byte[] binaryData) {
        return StringUtil.newStringUtf8(encodeBase64(binaryData, Integer.MAX_VALUE));
    }

	private static byte[] encodeBase64(byte[] binaryData, final int maxResultSize) {
		if (binaryData == null || binaryData.length == 0) {
			return binaryData;
		}
		
		final Base64 b64 = new Base64(0, CHUNK_SEPARATOR);
		final long len = b64.getEncodedLength(binaryData);
		if (len > maxResultSize) {
			throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
					len +
					") than the specified maximum size of " +
					maxResultSize);
		}
		
		return b64.encode(binaryData);
	}
	
	private long getEncodedLength(final byte[] pArray) {
		long len = ((pArray.length + unencodedBlockSize-1)  / unencodedBlockSize) * (long) encodedBlockSize;
		if (lineLength > 0) { 
			len += ((len + lineLength-1) / lineLength) * chunkSeparatorLength;
		}
		return len;
	}
	private byte[] encode(final byte[] pArray) {
		if (pArray == null || pArray.length == 0) {
			return pArray;
		}
		final Context context = new Context();
		encode(pArray, 0, pArray.length, context);
		encode(pArray, 0, EOF, context); // Notify encoder of EOF.
		final byte[] buf = new byte[context.pos - context.readPos];
		readResults(buf, 0, buf.length, context);
		return buf;
	}
	
	private int readResults(final byte[] b, final int bPos, final int bAvail, final Context context) {
		if (context.buffer != null) {
			final int len = Math.min(available(context), bAvail);
			System.arraycopy(context.buffer, context.readPos, b, bPos, len);
			context.readPos += len;
			if (context.readPos >= context.pos) {
				context.buffer = null; // so hasData() will return false, and this method can return -1
			}
			return len;
		}
		return context.eof ? EOF : 0;
	}
	private int available(final Context context) {  // package protected for access from I/O streams
        return context.buffer != null ? context.pos - context.readPos : 0;
    }
	
	private void encode(final byte[] in, int inPos, final int inAvail, final Context context) {
		if (context.eof) {
			return;
		}
		// inAvail < 0 is how we're informed of EOF in the underlying data we're
		// encoding.
		if (inAvail < 0) {
			context.eof = true;
			if (0 == context.modulus && lineLength == 0) {
				return; // no leftovers to process and not using chunking
			}
			final byte[] buffer = ensureBufferSize(encodeSize, context);
			final int savedPos = context.pos;
			switch (context.modulus) { // 0-2
			case 0 : // nothing to do here
				break;
			case 1 : // 8 bits = 6 + 2
				// top 6 bits:
				buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 2) & MASK_6BITS];
				// remaining 2:
				buffer[context.pos++] = encodeTable[(context.ibitWorkArea << 4) & MASK_6BITS];
				// URL-SAFE skips the padding to further reduce size.
				if (encodeTable == STANDARD_ENCODE_TABLE) {
					buffer[context.pos++] = PAD;
					buffer[context.pos++] = PAD;
				}
				break;
				
			case 2 : // 16 bits = 6 + 6 + 4
				buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 10) & MASK_6BITS];
				buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 4) & MASK_6BITS];
				buffer[context.pos++] = encodeTable[(context.ibitWorkArea << 2) & MASK_6BITS];
				// URL-SAFE skips the padding to further reduce size.
				if (encodeTable == STANDARD_ENCODE_TABLE) {
					buffer[context.pos++] = PAD;
				}
				break;
			default:
				throw new IllegalStateException("Impossible modulus "+context.modulus);
			}
			context.currentLinePos += context.pos - savedPos; // keep track of current line position
			// if currentPos == 0 we are at the start of a line, so don't add CRLF
			if (lineLength > 0 && context.currentLinePos > 0) {
				System.arraycopy(lineSeparator, 0, buffer, context.pos, lineSeparator.length);
				context.pos += lineSeparator.length;
			}
		} else {
			for (int i = 0; i < inAvail; i++) {
				final byte[] buffer = ensureBufferSize(encodeSize, context);
				context.modulus = (context.modulus+1) % BYTES_PER_UNENCODED_BLOCK;
				int b = in[inPos++];
				if (b < 0) {
					b += 256;
				}
				context.ibitWorkArea = (context.ibitWorkArea << 8) + b; //  BITS_PER_BYTE
				if (0 == context.modulus) { // 3 bytes = 24 bits = 4 * 6 bits to extract
					buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 18) & MASK_6BITS];
					buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 12) & MASK_6BITS];
					buffer[context.pos++] = encodeTable[(context.ibitWorkArea >> 6) & MASK_6BITS];
					buffer[context.pos++] = encodeTable[context.ibitWorkArea & MASK_6BITS];
					context.currentLinePos += BYTES_PER_ENCODED_BLOCK;
					if (lineLength > 0 && lineLength <= context.currentLinePos) {
						System.arraycopy(lineSeparator, 0, buffer, context.pos, lineSeparator.length);
						context.pos += lineSeparator.length;
						context.currentLinePos = 0;
					}
				}
			}
		}
	}
	
	private byte[] ensureBufferSize(final int size, final Context context){
		if ((context.buffer == null) || (context.buffer.length < context.pos + size)){
			return resizeBuffer(context);
		}
		return context.buffer;
	}
	
	private byte[] resizeBuffer(final Context context) {
		if (context.buffer == null) {
			context.buffer = new byte[getDefaultBufferSize()];
			context.pos = 0;
			context.readPos = 0;
		} else {
			final byte[] b = new byte[context.buffer.length * DEFAULT_BUFFER_RESIZE_FACTOR];
			System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
			context.buffer = b;
		}
		return context.buffer;
	}
	
	private int getDefaultBufferSize() {
        return DEFAULT_BUFFER_SIZE;
    }
	
	public static byte[] decodeBase64(final String base64String) {
		return new Base64(0, CHUNK_SEPARATOR).decode(base64String);
	}
	private byte[] decode(final String pArray) {
        return decode(StringUtil.getBytesUtf8(pArray));
    }
	
	private byte[] decode(final byte[] pArray) {
		if (pArray == null || pArray.length == 0) {
			return pArray;
		}
		final Context context = new Context();
		decode(pArray, 0, pArray.length, context);
		decode(pArray, 0, EOF, context); // Notify decoder of EOF.
		final byte[] result = new byte[context.pos];
		readResults(result, 0, result.length, context);
		return result;
	}
	
	private void decode(final byte[] in, int inPos, final int inAvail, final Context context) {
		if (context.eof) {
			return;
		}
		if (inAvail < 0) {
			context.eof = true;
		}
		for (int i = 0; i < inAvail; i++) {
			final byte[] buffer = ensureBufferSize(decodeSize, context);
			final byte b = in[inPos++];
			if (b == PAD) {
				// We're done.
				context.eof = true;
				break;
			} else {
				if (b >= 0 && b < DECODE_TABLE.length) {
					final int result = DECODE_TABLE[b];
					if (result >= 0) {
						context.modulus = (context.modulus+1) % BYTES_PER_ENCODED_BLOCK;
						context.ibitWorkArea = (context.ibitWorkArea << BITS_PER_ENCODED_BYTE) + result;
						if (context.modulus == 0) {
							buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 16) & MASK_8BITS);
							buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
							buffer[context.pos++] = (byte) (context.ibitWorkArea & MASK_8BITS);
						}
					}
				}
			}
		}
		// Two forms of EOF as far as base64 decoder is concerned: actual
		// EOF (-1) and first time '=' character is encountered in stream.
		// This approach makes the '=' padding characters completely optional.
		if (context.eof && context.modulus != 0) {
			final byte[] buffer = ensureBufferSize(decodeSize, context);
			
			// We have some spare bits remaining
			// Output all whole multiples of 8 bits and ignore the rest
			switch (context.modulus) {
//	              case 0 : // impossible, as excluded above
			case 1 : // 6 bits - ignore entirely
				break;
			case 2 : // 12 bits = 8 + 4
				context.ibitWorkArea = context.ibitWorkArea >> 4; // dump the extra 4 bits
					buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
					break;
					case 3 : // 18 bits = 8 + 8 + 2
						context.ibitWorkArea = context.ibitWorkArea >> 2; // dump 2 bits
						buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
						buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
						break;
					default:
						throw new IllegalStateException("Impossible modulus "+context.modulus);
			}
		}
	}

}
