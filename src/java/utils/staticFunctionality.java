/**Copyright 2016, University of Messina.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class will be filled with all static functionality.
 * @author agalletta
 * @author Giuseppe Tricomi
 */
public class staticFunctionality {
    
    public static String toMd5(String original) {
        MessageDigest md;
        byte[] digest;
        StringBuffer sb;
        String hashed = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            digest = md.digest();
            sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hashed = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return hashed;
    }
}
