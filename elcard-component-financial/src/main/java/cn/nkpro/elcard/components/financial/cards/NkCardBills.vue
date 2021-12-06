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
                :data="data"
                :max-height="data && data.length>100?500:undefined"
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}">
            <vxe-column field="expireDate"  width="15%" title="到期日期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="billType"    width="14%" title="账单类别"></vxe-column>
            <vxe-column field="amount"      width="20%" align="right" title="账单金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="received"    width="20%" align="right" title="已收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="receivable"  width="20%" align="right" title="应收金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="state"       title="状态" :formatter="formatState"></vxe-column>
        </vxe-table>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin({})],
        methods:{
            formatState({row}){
                if(row.state===1)
                    return "激活";
                return "未激活";
            }
        }
    }
</script>

<style scoped>

</style>