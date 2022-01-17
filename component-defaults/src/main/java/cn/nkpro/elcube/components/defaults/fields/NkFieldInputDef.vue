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
    <div>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="最大长度">
                {{value.maxLength}}
                <a-input-number size="small" slot="edit" :min="1" :max="100" v-model="value.maxLength"></a-input-number>
            </nk-form-item>
          <nk-form-item title="正则校验表达式">
            {{value.pattern}}
            <a-input size="small" slot="edit" :min="1" :max="100" v-model="value.pattern"></a-input>
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