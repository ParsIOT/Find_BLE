package com.find.wifitool.Utils.Server;

/**
 * Created by root on 3/8/17.
 */

public enum SendOptionEnum {
    Get_Booths_List("/booth/viewBoothList/"),
    Get_Product_List("/booth/viewProductsList/"),
    Check_server("/en/booth/checkServer"),
    Get_Booth_Item("/booth/viewBoothProducts/"),
    Get_Advertisement("/en/booth/advertisementJson");

    private String url;

    SendOptionEnum(String url) {
        this.url = url;
    }

    public String url() {
        return url;
    }
}
