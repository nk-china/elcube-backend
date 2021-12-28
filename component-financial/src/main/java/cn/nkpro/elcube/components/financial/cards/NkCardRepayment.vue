<!--
	This file is part of ELCube.
	ELCube is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	ELCube is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
-->
<template>
    <nk-card>
        <vxe-table
                ref="xTable"
                row-key
                auto-resize
                keep-source
                resizable
                highlight-hover-row
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                size="mini"
                border=inner
                :scroll-y="{enabled: false}"
                :data="list"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}"
                :checkbox-config="{checkField: 'checked', trigger: 'row', checkMethod, visibleMethod, showHeader: editMode}"
                @checkbox-change="checkboxChange"
                @checkbox-all="checkboxAll"
        >
            <vxe-column field="docNumber"     width="18%" title="单据编号">
                <template v-slot="{row}">
                    <nk-doc-link :doc="{docId:row.targetDocId}">{{row.docNumber}}</nk-doc-link>
                </template>
            </vxe-column>
            <vxe-column field="expireDate"    width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="billType"      width="12%" title="账单类别"></vxe-column>
            <vxe-column field="amount"        width="12%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="received"      width="12%" align="right" title="已收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="receivable"    width="12%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="currReceived"  width="12%" align="right" title="本次偿还" formatter="nkCurrency"></vxe-column>
            <vxe-column field="checked" type="checkbox"></vxe-column>
        </vxe-table>
        <vxe-pager
                v-if="!editMode"
                size="mini"
                :current-page="page.page"
                :page-size="page.size"
                :total="data.length"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'Sizes', 'Total']"
                @page-change="handlePageChange">
        </vxe-pager>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin({})],
        data(){
            return {
                page:{
                    page:1,
                    size:15,
                },
            }
        },
        computed:{
            list(){
                if(this.editMode)
                    return this.data;

                return this.data.slice(
                    (this.page.page - 1) * this.page.size,
                    this.page.page      * this.page.size
                )
            }
        },
        methods:{
            nk$editModeChanged(editMode){
                if(editMode){
                    this.nk$calc("init");
                }
            },
            checkboxAll({checked}){
                this.$refs.xTable.setAllCheckboxRow(checked);
                this.nk$calc("checkChange");
            },
            checkboxChange({row}){

                if(row.checked){
                    this.$refs.xTable.setCheckboxRow(
                        this.data.slice(0, this.data.indexOf(row)),
                        true
                    );
                }else{
                    this.$refs.xTable.setCheckboxRow(
                        this.data.slice(this.data.indexOf(row)),
                        false
                    );
                }

                this.nk$calc("checkChange");
            },
            handlePageChange({ currentPage, pageSize }){
                this.page.page = currentPage;
                this.page.size = pageSize;
            },
            visibleMethod(){
                return this.editMode
            },
            checkMethod(){
                return this.editMode
            }
        }
    }
</script>

<style scoped>

</style>