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
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="格式">
                {{value.format}}
                <a-input size="small" slot="edit" v-model="value.format"></a-input>
            </nk-form-item>
            <nk-form-item title="最小值">
                {{value.min}}
                <a-input-number size="small" slot="edit" v-model="value.min"></a-input-number>
            </nk-form-item>
            <nk-form-item title="最大值">
                {{value.max}}
                <a-input-number size="small" slot="edit" v-model="value.max"></a-input-number>
            </nk-form-item>
            <nk-form-item title="Digits">
                {{value.digits}}
                <a-input-number slot="edit" size="small" :min="0" :max="6" :precision="0" v-model="value.digits"></a-input-number>
            </nk-form-item>
            <nk-form-item title="Step">
                {{value.step}}
                <a-input-number slot="edit" size="small" :min="0.0001" :max="10000" :precision="2" v-model="value.step"></a-input-number>
            </nk-form-item>
        </nk-form>
    </div>
</template>

<script>
export default {
    props:{
        editMode: Boolean,
        value: Object
    }
}
</script>

<style scoped>

</style>