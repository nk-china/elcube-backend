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
            <nk-form-item title="选中后显示">
                {{value.checked}}
                <a-input slot="edit" size="small" :maxLength="20" v-model="value.checked"></a-input>
            </nk-form-item>
            <nk-form-item title="未选中显示">
                {{value.unChecked}}
                <a-input slot="edit" size="small" :maxLength="20" v-model="value.unChecked"></a-input>
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