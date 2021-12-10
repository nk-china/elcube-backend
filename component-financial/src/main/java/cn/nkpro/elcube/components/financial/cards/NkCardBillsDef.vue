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
    <nk-def-card>
        <nk-form :edit="editMode" :col="1">
            <nk-form-item title="激活条件">
                <nk-sp-el-view :el="def.activeSpEL"></nk-sp-el-view>
                <nk-sp-el-editor slot="edit" v-model="def.activeSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="还款计划卡片">
                {{def.paymentCardKey}}
                <a-input slot="edit" v-model="def.paymentCardKey"></a-input>
            </nk-form-item>
            <nk-form-item title="账单源">
                <nk-sp-el-view :el="def.collectSpEL"></nk-sp-el-view>
                <nk-sp-el-editor slot="edit" v-model="def.collectSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="违约金账单类别">
                {{def.overdueBillType}}
                <a-input slot="edit" size="small" v-model="def.overdueBillType"></a-input>
            </nk-form-item>
            <nk-form-item title="违约金利率(%)">
                {{def.overdueBillRate}}
                <a-input-number slot="edit" size="small" :min="0" :max="100" v-model="def.overdueBillRate"></a-input-number>
            </nk-form-item>
            <nk-form-item title="计算违约金的账单类别">
                {{def.overdueBillDefs}}
                <a-select slot="edit" size="small" mode="tags" v-model="def.overdueBillDefs"></a-select>
            </nk-form-item>
        </nk-form>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";
    export default {
        mixins:[new MixinDef({})],
    }
</script>

<style scoped>

</style>