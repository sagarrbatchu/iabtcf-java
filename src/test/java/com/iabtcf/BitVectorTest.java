package com.iabtcf;

/*-
 * #%L
 * IAB TCF Core Library
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.iabtcf.utils.ByteBitVectorUtils;

@Deprecated
public class BitVectorTest {
    @Test
    public void testCanReadSmallInt() {
        String bitString = "0000 1000 0000 0001";
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(2, bitVector.readBits6(0));
    }

    @Test
    public void tesCanReadInstantFromDeciSecond() {
        String bitString = "1 001110101101110010100111000111000100 1";
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(
                Instant.parse("2020-01-26T18:19:25.200Z"), Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void tesCanReadEpochInstantFromDeciSecond() {
        String bitString = Stream.generate(() -> "0").limit(36).collect(Collectors.joining());
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals(Instant.EPOCH, Instant.ofEpochMilli(bitVector.readBits36(1) * 100));
    }

    @Test
    public void testCanReadBit() {
        String bitString = "10101010";
        ByteBitVector bitVector = fromBitString(bitString);
        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                assertTrue(bitVector.readBits1(i));
            } else {
                assertFalse(bitVector.readBits1(i));
            }
        }
    }

    @Test
    public void testReadSixBitString() {
        String bitString = "000000 000001";
        ByteBitVector bitVector = fromBitString(bitString);
        assertEquals("AB", ByteBitVectorUtils.readStr2(bitVector, 0));
    }

    private ByteBitVector fromBitString(String bits) {
        ByteBitVector bitVector = new ByteBitVector(fromString(bits));
        return bitVector;
    }

    private byte[] fromString(String bitString) {
        String spaceTrimmed = bitString.replaceAll(" ", "");
        byte[] bytes = new byte[(int) Math.ceil(spaceTrimmed.length() / 8.0)];
        int j = 0;
        for (int i = 0; i < spaceTrimmed.length(); i += 8) {
            int endIndex;
            if (i + 8 < spaceTrimmed.length()) {
                endIndex = i + 8;
            } else {
                endIndex = spaceTrimmed.length();
            }
            String sub = spaceTrimmed.substring(i, endIndex);
            sub =
                    sub.length() == 8
                            ? sub
                            : sub
                            + Stream.generate(() -> "0")
                            .limit(8 - sub.length())
                            .collect(Collectors.joining());
            bytes[j++] = (byte) (Integer.parseInt(sub, 2));
        }
        return bytes;
    }
}
