package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.cards.NkCardDate;

public class TestDoc {

    public static void main(String[] args) {

        try {
            NkCardDate nkCardDate = new NkCardDate();

            System.out.println(nkCardDate.create(null,null,null));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
