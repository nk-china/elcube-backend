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