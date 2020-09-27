package com.offcn.group;

import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class Goods  implements Serializable {

    private TbGoods tbGoods;
    private TbGoodsDesc tbGoodsDesc;
    private List<TbItem> itemList;

    public Goods(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc, List<TbItem> itemList) {
        this.tbGoods = tbGoods;
        this.tbGoodsDesc = tbGoodsDesc;
        this.itemList = itemList;
    }

    public Goods() {
    }

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
