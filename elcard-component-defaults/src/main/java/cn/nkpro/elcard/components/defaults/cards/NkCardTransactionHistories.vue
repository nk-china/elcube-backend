<!--
	This file is part of ELCard.
	ELCard is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCard is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
-->
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
            <vxe-table-column key="docTypeDesc"   title="交易类型"  field="docTypeDesc" width="26%"  />
            <vxe-table-column key="docName"       title="交易描述"  field="docName"     width="20%"  />
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