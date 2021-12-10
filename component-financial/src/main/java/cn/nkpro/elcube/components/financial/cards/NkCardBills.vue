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
                :data="list"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}">
            <vxe-column field="expireDate"  width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="billType"    width="14%" title="账单类别">
                <template v-slot="{row}">
                    <a v-if="def.overdueBillType===row.billType" @click="openDetail(row)">
                        {{row.billType}}
                        <a-icon v-if="row._loading" type="loading" />
                    </a>
                    <template v-else>{{row.billType}}</template>
                </template>
            </vxe-column>
            <vxe-column field="amount"      width="20%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="received"    width="20%" align="right" title="已收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="receivable"  width="20%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="state"       title="状态" :formatter="formatState"></vxe-column>
        </vxe-table>
        <vxe-pager
                size="mini"
                :current-page="page.page"
                :page-size="page.size"
                :total="data.length"
                :layouts="['PrevPage', 'JumpNumber', 'NextPage', 'Sizes', 'Total']"
                @page-change="handlePageChange">
        </vxe-pager>
        <a-modal :title="def.overdueBillType+'明细'" width="60%" v-model="detailsVisible" centered >
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
                    :data="detailsList"
                    :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}">
                <vxe-column field="expireDate"  width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
                <vxe-column field="billType"    width="12%" title="账单类别"></vxe-column>
                <vxe-column field="amount"      width="18%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="received"    width="18%" align="right" title="已收金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="receivable"  width="18%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="overdueAmount"           align="right" :title="def.overdueBillType" formatter="nkCurrency"></vxe-column>
            </vxe-table>
            <template slot="footer">
                <a-button key="submit" type="primary" @click="detailsVisible=false">
                    关闭
                </a-button>
            </template>
        </a-modal>
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
                details: undefined,
                detailsVisible: false
            }
        },
        computed:{
            list(){
                return this.data.slice(
                    (this.page.page - 1) * this.page.size,
                     this.page.page      * this.page.size
                )
            },
            detailsList(){
                if(this.details)
                    return JSON.parse(this.details)
            }
        },
        methods:{
            formatState({row}){
                if(row.state===1)
                    return "激活";
                return "未激活";
            },
            handlePageChange({ currentPage, pageSize }){
                this.page.page = currentPage;
                this.page.size = pageSize;
            },
            openDetail(row){
                if(row.details){
                    this.details = row.details;
                    this.detailsVisible = true;
                }else{
                    this.$set(row,'_loading',true);
                    this.nk$call(row)
                        .then(res=>{
                            this.details = res.details;
                            row.details = res.details;
                            this.$set(row,'_loading',false);
                            this.detailsVisible = true;
                        })
                }
            }
        }
    }
</script>

<style scoped>

</style>