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
    <nk-def-card>
        <nk-form :edit="editMode" :col="1">
            <nk-form-item title="激活条件">
                {{def.activeSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.activeSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="还款计划卡片">
                {{def.paymentCardKey}}
                <a-input slot="edit" v-model="def.paymentCardKey"></a-input>
            </nk-form-item>
            <nk-form-item title="账单源">
                {{def.collectSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.collectSpEL"></nk-sp-el-editor>
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