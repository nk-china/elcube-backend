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

        <a-button-group v-if="views" style="margin-bottom: 16px;">
            <a-button v-for="view in views"
                      size="small"
                      :key="view.name"
                      :type="selected === view ? 'primary' : 'dashed'"
                      @click="switchView(view)">
                {{view.name}}
            </a-button>
        </a-button-group>

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
                class="mytable-style"
                :row-class-name="rowClassName"
                :data="list"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}">
            <vxe-column field="expireDate"  width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="billType"    width="14%" title="账单类别">
                <template v-slot="{row}">
                    <a v-if="def.overdueBillType===row.billType" @click="openDetail(row)">
                        {{row.billType}}
                        <a-icon v-if="row._loading1" type="loading"></a-icon>
                    </a>
                    <template v-else>{{row.billType}}</template>
                </template>
            </vxe-column>
            <vxe-column field="amount"      width="20%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="received"    width="20%" align="right" title="已收金额">
                <template v-slot="{row}">
                    <a @click="openRepayment(row)">
                        {{row.received | nkCurrency}}
                        <a-icon v-if="row._loading2" type="loading"></a-icon>
                    </a>
                </template>
            </vxe-column>
            <vxe-column field="receivable"  width="20%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="state"       title="状态" :formatter="formatState" class-name="state"></vxe-column>
        </vxe-table>
        <vxe-pager
                size="mini"
                :current-page="page.page"
                :page-size="page.size"
                :total="viewData.length"
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
        <a-modal :title="'核销明细'" width="60%" v-model="repaymentsVisible" centered >
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
                    :data="repayments">
                <vxe-column field="billType"     width="10%" title="账单类别"></vxe-column>
                <vxe-column field="expireDate"   width="14%" title="到期日期" formatter="nkDatetime"></vxe-column>
                <vxe-column field="amount"       width="14%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="receivable"   width="14%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="currReceived" width="14%" align="right" title="实收金额" formatter="nkCurrency"></vxe-column>
                <vxe-column field="createdTime"  width="14%" title="核销日期" formatter="nkDatetime"></vxe-column>
                <vxe-column field="accountDate"  width="14%" title="记账日期" formatter="nkDatetime"></vxe-column>
                <vxe-column title="#">
                    <template v-slot="{row}">
                        <nk-doc-link :doc="row" @click="repaymentsVisible=false">详情</nk-doc-link>
                    </template>
                </vxe-column>
            </vxe-table>
            <template slot="footer">
                <a-button key="submit" type="primary" @click="repaymentsVisible=false">
                    关闭
                </a-button>
            </template>
        </a-modal>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";
    import moment from "moment";

    const today = moment(0, "HH").unix();

    export default {
        mixins:[new Mixin({})],
        data(){
            return {
                page:{
                    page:1,
                    size:15,
                },
                details: undefined,
                detailsVisible: false,

                repayments: undefined,
                repaymentsVisible:false,

                selectedView: undefined
            }
        },
        computed:{
            views(){
                if(this.def.viewDefs){
                    return JSON.parse(this.def.viewDefs)
                }
            },
            selected(){
                return this.selectedView || (this.views && this.views[0]);
            },
            viewData(){
                if(this.selected && this.selected.includes){
                    return this.data.filter(i=>{
                        return this.selected.includes.indexOf(i.billType)>-1
                    });
                }
                return this.data;
            },
            list(){

                return this.viewData.slice(
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
                if(row.state===1){
                    if(row.expireDate<today && row.receivable){
                        return '逾期';
                    }
                    return "激活";
                }
                return "未激活";
            },
            handlePageChange({ currentPage, pageSize }){
                this.page.page = currentPage;
                this.page.size = pageSize;
            },
            switchView(view){
                this.selectedView = view;
                this.page.page = 1;
            },
            openDetail(row){
                if(row.details){
                    this.details = row.details;
                    this.detailsVisible = true;
                }else{
                    this.$set(row,'_loading1',true);
                    this.nk$call(Object.assign({operator:'details'},row))
                        .then(res=>{
                            this.details = res.details;
                            row.details  = res.details;
                            this.$set(row,'_loading1',false);
                            this.detailsVisible = true;
                        })
                }
            },
            openRepayment(row){
                if(row.repayments){
                    this.repayments = row.repayments;
                    this.repaymentsVisible = true;
                }else{
                    this.$set(row,'_loading2',true);
                    this.nk$call(Object.assign({operator:'repayments'},row))
                        .then(res=>{
                            this.repayments = res;
                            row.repayments  = res;
                            this.$set(row,'_loading2',false);
                            this.repaymentsVisible = true;
                        })
                }
            },
            rowClassName ({ row }) {
                if(row.expireDate<today && row.receivable){
                    return 'overdue';
                }
            }
        }
    }
</script>

<style scoped lang="less">
    ::v-deep.mytable-style .overdue {
        background-color: #fffbe6;

        .state{
            color: #f5222d;
        }
    }
</style>