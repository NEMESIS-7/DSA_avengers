package structures.b4_hash_table;


public class Config {

    // S = sum of the last three digits of every member's index number
    public static final int S = 6316;

    // hashTableSize = smallest prime >= 1000 + (S mod 500)
    // 6316 mod 500 = 316, so 1000 + 316 = 1316, smallest prime >= 1316 is 1319
    public static final int HASH_TABLE_SIZE = 1319;
}