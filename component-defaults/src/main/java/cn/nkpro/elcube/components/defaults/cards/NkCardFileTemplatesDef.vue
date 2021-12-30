<template>
    <nk-def-card>
        <vxe-toolbar v-if="editMode">
            <template v-slot:buttons>
                <vxe-button icon="fa fa-plus" status="perfect" size="mini" @click="add()">新增</vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table
                ref="xTable"
                row-key
                auto-resize
                size="mini"
                border=inner
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                resizable
                highlight-hover-row
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod}"
                @edit-actived="tableEditActived"
                :data="def.items">
            <vxe-table-column title="模版项" field="templateCode" width="8%"
                              :edit-render="{name: 'input', attrs: {type: 'text'}}"/>
            <vxe-table-column title="描述" field="templateDesc" width="10%"
                              :edit-render="{name: 'input', attrs: {type: 'text'}}"/>
            <vxe-table-column title="文件模版" field="templateBase64" width="20%" :edit-render="{}">
                <template v-slot="{row}">
                    {{row.templateFileName}}
                    <a-button type="link" size="small"
                              @click="exportFile(row)"
                              :loading="row.loading"
                              style="font-size:12px">下载
                    </a-button>
                </template>
                <template v-slot:edit>
                    <a-upload :before-upload="beforeUpload" size="small">
                        <a-button>
                            <a-icon type="upload"/>
                            Select File
                        </a-button>
                    </a-upload>
                </template>
            </vxe-table-column>
            <vxe-table-column title="生效条件SpEL" field="templateSpEL" width="20%"
                              :edit-render="{name: 'input', attrs: {type: 'text'}}"/>

            <vxe-table-column title="导出类型" width="10%" field="exportType" :edit-render="{name: '$select',
                              options:   [{label:'word',value:'0'},
                                           {label:'pdf',value:'1'}]}">
            </vxe-table-column>
            <vxe-table-column title="导出文件时的处理程序" field="handler" width="20%"
                              :edit-render="{name: '$select',
                              options:handlerClassOptions   }"/>

            <vxe-table-column title="" field="">
                <template v-slot="{seq,items}">
                    <span v-if="editMode && sortable" class="drag-btn" style="margin-left: 10px;">
                        <i class="vxe-icon--menu"></i>
                    </span>
                    <span v-if="editMode" style="margin-left: 10px;" @click="$nkSortableRemove(def.items,seq)">
                        <i class="vxe-icon--remove"></i>
                    </span>
                </template>
            </vxe-table-column>
        </vxe-table>
        <iframe ref="iframe" style="display: none"></iframe>
    </nk-def-card>
</template>

<script>
    import {MixinDef, MixinSortable,NkUtil} from "nk";
    export default {
        mixins: [new MixinDef({}), MixinSortable()],
        data() {
            return {
                handlerClassOptions:[],
                row: undefined,
                sortable:true,
                pathUrl:""
            }
        },
        created() {
            this.findHandlerClassOp();
        },
        methods: {
            findHandlerClassOp(){
                this.nk$callDef("findHandlerClassOp").then(response =>{
                    this.handlerClassOptions = response.map(item=>({
                        label: item.name,
                        value: item.key
                    }));
                });
            },
            tableEditActived({row}){
                this.row = row;
            },
            add() {
                this.def.items = this.def.items || [];
                const newItem = {exportType:''};
                this.def.items.push(newItem);
                this.$refs.xTable.loadData(this.def.items).then(() => this.$refs.xTable.setActiveRow(newItem));
            },
            beforeUpload(file) {
                const reader = new FileReader();
                const self = this;
                reader.readAsDataURL(file);
                reader.onload = function () {
                    self.row.templateBase64 = this.result;
                    self.row.templateFileName = file.name;
                }
                return false;
            },
            exportFile(row) {
                this.nk$callDef(row.templateCode)
                    .then(res => {this.pathUrl = res});
                this.$http.get("/api/fs/download?url="+this.pathUrl).then(res => {
                    this.$refs.iframe.src = res.data;
                });
            },
             hasError() {
                if(NkUtil.hasBlack(this.def.items,['templateCode'])) {
                    return this.$message.error("模板不能为空，请检查后再次提交");
                }
                if(NkUtil.isRepeat(this.def.items,['templateCode'])) {
                    this.$message.error("模板定义重复，请检查后再次提交");
                    return false;
                }
                if(NkUtil.hasBlack(this.def.items,['templateBase64'])) {
                    return this.$message.error("文件不能为空，请检查后再次提交");
                }
                if(NkUtil.hasBlack(this.def.items,['templateDesc'])) {
                    return this.$message.error("描述不能为空，请检查后再次提交");
                }
            }
        }
    }
</script>

<style scoped>
</style>
