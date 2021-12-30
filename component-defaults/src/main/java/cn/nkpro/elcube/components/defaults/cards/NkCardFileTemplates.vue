<template>
    <nk-card  :edit-mode="editMode">
        <vxe-table
                ref="xTable"
                auto-resize
                keep-source
                resizable
                highlight-hover-row
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                size="mini"
                border=inner
                :data="def.items"
                :loading="loading"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, activeMethod: xTableActiveMethod, showStatus: true}"
                :sort-config="{trigger: 'cell', remote: false,showIcon: !editMode, orders: ['desc', 'asc', null]}"
                :class="editMode&&'edit-table'">
            <vxe-table-column title="模版项"           field="templateCode" />
            <vxe-table-column title="描述"             field="templateDesc"  />
            <vxe-table-column title="Action"          field="" v-if="!editMode" >
                <template v-slot="{row}">
                    <a-button type="link"
                              size="small"
                              @click="exportFile(row)"
                              :loading="row.loading"
                              style="font-size:12px"
                    >导出</a-button>
                </template>
            </vxe-table-column>
        </vxe-table>
        <iframe ref="iframe" style="display: none"></iframe>
    </nk-card>
</template>

<script>
    import {Mixin, MixinSortable} from "nk";
    export default {
        mixins:[new Mixin({}),new MixinSortable()],
        data(){
            return {
                loading: false,
                trigger: false,
            }
        },
        methods:{
            xTableActiveMethod(){
                return this.editMode;
            },
            exportFile(row){
                this.$set(row,"_loading",true);
                this.nk$call("export:"+row.templateCode)
                    .then(res=>{
                        this.$set(row,"_loading",false);
                        this.$http.get("/api/fs/download?url="+res).then(res => {
                            console.log("res",res)
                            this.$refs.iframe.src = res.data;
                        });
                    })
                    .catch(()=>{
                        this.$set(row,"_loading",false);
                    });
            }
        }
    }
</script>

<style scoped>
</style>
