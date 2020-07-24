package com.leyou.auth;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:\\heima\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\heima\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        //secret相当于盐
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjgsInVzZXJuYW1lIjoibGV5b3UxIiwiZXhwIjoxNTk1Mzk0ODM2fQ.JigPaYWwdePzgn8uCYXF14HLrAfsxTfHwzsWB6IKlhea3Eq_Rf4aVs9f-sz97022ZMIynQTbPJXtYdPOomSSROZOsi89n1MInytoaJcsm-a7HuOovGytrGIVDKvpL656pe20Vdj6LxO76BQsLcGshcnV1C8TTFAYt_XtXj5RqjM";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
