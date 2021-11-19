<template>
    <nk-card>
        <a-button-group slot="extra" size="small">
            <a-button type="dashed" @click="$refs.table.clearTreeExpand()">
                <a-icon type="up" />
            </a-button>
            <a-button type="dashed" @click="$refs.table.setAllTreeExpand(true)">
                <a-icon type="down" />
            </a-button>
        </a-button-group>
        <vxe-table
                ref="table"
                auto-resize
                size="mini"
                border="inner"
                highlight-hover-row
                show-overflow
                :tree-config="{
                children: 'children',
                expandAll:true,
                indent:'10',
                line: this.data.length<=100
            }"
                :data="tree">
            <vxe-table-column key="docNumber"    title="#"      field="docNumber" width="18%"  :tree-node="true">
                <template v-slot="{ row }">
                    <nk-doc-link :doc="row" v-if="row.docId!==doc.docId">{{row.docNumber}}</nk-doc-link>
                    <span v-else style="color: #2b2b2b;font-weight: bold;">{{row.docNumber}}</span>
                </template>
            </vxe-table-column>
            <vxe-table-column key="docType"       title="交易代码"  field="docType"     width="10%"  />
            <vxe-table-column key="docName"       title="交易名称"  field="docName"     width="16%"  />
            <vxe-table-column key="docDesc"       title="交易描述"  field="docDesc"     width="20%"  />
            <vxe-table-column key="docStateDesc"  title="状态"     field="docStateDesc" width="16%" />
            <vxe-table-column key="updatedTime"   title="更新时间"  field="updatedTime"  formatter="nkDatetimeFriendly" />
        </vxe-table>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin({field:undefined})],
        data(){
            return {
                tree:undefined
            }
        },
        created() {
            this.nk$call()
                .then(data=>{
                    if(data&&data.list){
                        const tree = [];
                        const hash = [];

                        data.list.forEach(item=>{
                            const parent = hash.find(t=>t.docId===item.preDocId);
                            if(parent){
                                if(!parent.children){
                                    parent.children = [];
                                }
                                parent.children.push(item);
                            }else{
                                tree.push(item);
                            }
                            hash.push(item);
                        });
                        this.tree = tree;
                    }
                }).catch(res=>{
                    console.log(res)
                })
        }
    }
</script>

<style scoped>

</style>