package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import lombok.Getter;

public abstract class NkDocFlowInterceptor extends NkAbstractCustomScriptObject {
    public abstract FlowDescribe apply(DocHV docHV);


    public static class FlowDescribe{

        @Getter
        private boolean visible;

        @Getter
        private String visibleDesc;

        private FlowDescribe(boolean visible,String visibleDesc){
            this.visible = visible;
            this.visibleDesc = visibleDesc;
        }

        public static FlowDescribe visible(){
            return new FlowDescribe(true,null);
        }
        public static FlowDescribe invisible(String visibleDesc){
            return new FlowDescribe(false,visibleDesc);
        }
    }
}
