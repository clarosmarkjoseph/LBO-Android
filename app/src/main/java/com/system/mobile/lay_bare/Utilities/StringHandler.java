package com.system.mobile.lay_bare.Utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by paolohilario on 1/12/18.
 */

public class StringHandler {

    public String  replaceSpaceToCharacter(String url){
        return url.replace(" ","%20");
    }

}
