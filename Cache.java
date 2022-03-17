
public class Cache {
    private String name;
    private int addressSize;
    private int wordSize;
    private int blockSize;
    private int numLines;
    private int offsetBits;
    private int blockNumBits;
    private int indexBits;
    private int tagBits;
    private int requests = 0;
    private int hits = 0;
    private int misses = 0;
    private boolean[] valid;
    private int[] tags;

    public Cache(String name, int addressSize, int wordSize, int blockSize, int numLines) {
        this.name = name;
        this.addressSize = addressSize;
        this.wordSize = wordSize;
        this.blockSize = blockSize;
        this.numLines = numLines;
        computeCacheData();
        valid = new boolean[numLines];
        tags = new int[numLines];
    }

    /**
     * Computes log2 of an integer
     * @param n input
     * @return int value of log2(n)
     */
    private int log2(int n) {
        return (int)(Math.log(n) / Math.log(2));
    }

    /**
     * Private method to compute all cache data related to bits count
     */
    private void computeCacheData() {
        // computing offset bits
        // first, we compute number of bytes of a word
        int wordSizeInBytes = wordSize / 8;
        offsetBits = log2(wordSizeInBytes) + log2(blockSize);
        // blockNumBits is calculated depending on number of words in a block
        blockNumBits = addressSize - tagBits - offsetBits;
        // indexBits is calculated depending on number of lines in cache memory
        indexBits = log2(numLines);
        // then, tagBits is the difference between addressSize and sum of other bits computed before
        tagBits = addressSize - offsetBits - indexBits;
    }

    public void readLocation(int address) {
        int blockAddress = address;
        blockAddress >>>= offsetBits;

        int offset = 0xFFFFFFFF;
        offset <<= offsetBits;
        offset = ~offset;
        offset &= address;

        int index = address;
        index >>>= offsetBits;
        index = (int) (index % (Math.pow(2, indexBits)));

        int tag = address;
        tag >>>= (offsetBits + indexBits);

        requests++;
        boolean hit = false;
        if (!valid[index]) {
            valid[index] = true;
            tags[index] = tag;
            misses++;
        } else {
            if (tags[index] == tag) {
                hits++;
                hit = true;
            } else {
                misses++;
            }
        }

        System.out.println("Read Mem : " + address + " (" + binary(address, addressSize) + ")\n" +
                "Block Addr: " + blockAddress + " (" + binary(blockAddress, addressSize - offsetBits) + ")\n" +
                "Offset : " + offset + " (" + binary(offset, offsetBits)+ ")\n" +
                "Block Num : " + index + " (" + binary(index, indexBits) + ")\n" +
                "Tag : " + tag + " (" + binary(tag, tagBits) + ")\n" +
                "Result : " + (hit ? "** Hit **" : "Miss"));
    }

    public void print() {
        long W = (long) Math.pow(2, addressSize) / (wordSize / 8);
        int S = wordSize;
        long T = (long) Math.pow(2, addressSize);
        int L = numLines;
        int X = tagBits;
        int B = blockSize;
        int C = (1 + tagBits) * numLines + numLines * blockSize * wordSize;
        System.out.println("********** " + name + " Cache Size Report **********\n" +
                "Memory : " + W + " words of " + S + " bits (" + T + " bytes)\n" +
                "Cache : " + L + " lines with " + X + " bits of tag, 1 bit for the valid flag and " + B + " words of data each (" + C + " bits)");
    }

    public void stats() {
        double hr = (double) hits / (double) (requests) * 100;
        double mr = (double) misses / (double) (requests) * 100;

        System.out.println("**********\n" +
                "********** " + name + " Cache Stats Report\n" +
                "********** Fall 2021\n" +
                "**********\n" +
                "Requests: " + requests + "\n" +
                "Hits : " + hits + " (" + hr +"%)\n" +
                "Misses : " +  misses + " ("+ mr +"%)");
    }

    private String binary(int x, int size) {
        return String.format("%32s", Integer.toBinaryString(x)).
                replace(" ", "0").substring(32-size);
    }
}
