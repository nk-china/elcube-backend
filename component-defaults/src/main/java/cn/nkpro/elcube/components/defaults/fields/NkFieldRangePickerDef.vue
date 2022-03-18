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
      <nk-form-item title="选择区间">
        {{value.range}}
        <nk-sp-el-editor slot="edit" v-model="value.range"></nk-sp-el-editor>
      </nk-form-item>
      <nk-form-item title="显示格式">
        {{value.format}}
        <a-input size="small" slot="edit" v-model="value.format"></a-input>
      </nk-form-item>
      <nk-form-item title="存储格式">
        {{value.formatStorage}}
        <a-input size="small" slot="edit" v-model="value.formatStorage"></a-input>
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