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
        <nk-form :col="1">
            <nk-form-item term="创建时间">{{doc.createdTime | nkDatetimeFriendly}}</nk-form-item>
            <nk-form-item term="更新时间">{{doc.updatedTime | nkDatetimeFriendly}}</nk-form-item>
            <nk-form-item term="单据版本">{{doc.defVersion}}</nk-form-item>
            <nk-form-item term="配置版本">{{doc.def.version}}</nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
    mixins:[new Mixin()],
}
</script>

<style scoped>

</style>