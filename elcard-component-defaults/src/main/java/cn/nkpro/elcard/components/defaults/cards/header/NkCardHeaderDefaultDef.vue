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
    <nk-def-card>
        <nk-form :col="1" :edit="editMode">
            <nk-form-item title="伙伴ID SpEL">
                {{def.partnerIdSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerIdSpEL"></nk-sp-el-editor>
            </nk-form-item>
            <nk-form-item title="伙伴名称SpEL">
                {{def.partnerNameSpEL}}
                <nk-sp-el-editor slot="edit" v-model="def.partnerNameSpEL"></nk-sp-el-editor>
            </nk-form-item>
        </nk-form>
    </nk-def-card>
</template>

<script>
    import MixinDef from "MixinDef";

    export default {
        mixins:[new MixinDef({})],
        computed:{
        },
        created() {
        }
    }
</script>

<style scoped>

</style>