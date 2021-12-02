<!--
	This file is part of EAsis.
	EAsis is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	EAsis is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.
	You should have received a copy of the GNU Affero General Public License
	along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
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
                :edit-config="{trigger: 'click', mode: 'row', showIcon: editMode, showStatus: true}">
            <vxe-column field="period"      width="8%"  title="期次"></vxe-column>
            <vxe-column field="expireDate"  width="12%" title="到期" formatter="nkDatetime"></vxe-column>
            <vxe-column field="pay"         width="15%" align="right" title="付款金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="principal"   width="14%" align="right" title="本金" formatter="nkCurrency"></vxe-column>
            <vxe-column field="interest"    width="14%" align="right" title="利息" formatter="nkCurrency"></vxe-column>
            <vxe-column field="fee"         width="10%" align="right" title="其他费用" formatter="nkCurrency"></vxe-column>
            <vxe-column field="residual"    width="17%" align="right" title="剩余金额" formatter="nkCurrency"></vxe-column>
            <vxe-column field="remark"      width="10%" title="备注"></vxe-column>
        </vxe-table>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";
    export default {
        mixins:[new Mixin({})],
    }
</script>

<style scoped>

</style>