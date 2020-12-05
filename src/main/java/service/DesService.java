package service;

import pojo.ChampionshipPojo;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class DesService {
    private SecretKey key;
    private Cipher cipher;

    public DesService(String key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public List<ChampionshipPojo> decrypt(String data) throws InvalidKeyException, IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, key);

        SealedObject sealedObject = null;
        ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(HexService.fromHex(data))));
        sealedObject = (SealedObject) in.readObject();
        List<ChampionshipPojo> championships = (List<ChampionshipPojo>) sealedObject.getObject(cipher);
        in.close();

        return championships;
    }
}