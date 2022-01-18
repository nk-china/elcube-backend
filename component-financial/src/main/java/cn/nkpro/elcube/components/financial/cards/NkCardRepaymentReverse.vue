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
        >
            <vxe-column field="docNumber"     width="18%" title="单据编号">
                <template v-slot="{row}">
                    <nk-doc-link :doc="{docId:row.targetDocId}">{{row.docNumber}}</nk-doc-link>
                </template>
            </vxe-column>
            <vxe-column field="expireDate"    width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="billType"      width="10%" title="账单类别"></vxe-column>
            <vxe-column field="amount"        width="12%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="received"      width="12%" align="right" title="收款金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="accountDate"   width="15%" title="收款日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="currReceived"  width="12%" align="right" title="冲红金额" formatter="nkCurrency"></vxe-column>
            <vxe-column ></vxe-column>
        </vxe-table>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin({})],
        data(){
            return {
            }
        },
        computed:{
            list(){
                return this.data
            }
        },
        methods:{
            nk$editModeChanged(editMode) {
                if(!this.doc.newCreate && editMode){
                    this.nk$calc('redo');
                }
            }
        }
    }
</script>

<style scoped>

</style>