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
    <nk-card>
        <nk-form :edit="editMode" :col="2">
            <nk-form-item title="关联用户账号">
                <router-link v-if="data.account" :to="`/apps/settings/auth/accounts/detail/${data.account}`">{{data.account}}</router-link>
                <a-select
                    show-search
                    :value="data.account"
                    placeholder="input search text"
                    style="width: 200px"
                    :default-active-first-option="false"
                    :show-arrow="false"
                    :filter-option="false"
                    :not-found-content="null"
                    @search="handleSearch"
                    @change="handleChange"
                    slot="edit"
                >
                    <a-select-option v-for="d in accountList" :key="d.value">
                      {{ d.text }}
                    </a-select-option>
                </a-select>
            </nk-form-item>
        </nk-form>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";
    export default {
        mixins:[Mixin()],
        data(){
          return{
            accountList:[]
          }
        },
        methods:{
          handleSearch(value){
            this.nk$call(value).then(res =>{
              let accounts = [];
              res.forEach(function (element, index, array) {
                let obj = {
                  text: element.username,
                  value: element.username
                }
                accounts.push(obj);
              });
              this.accountList = accounts;
              this.data.account = value;
            })
          },
          handleChange(value){
            this.data.account = value;
          }
        }
    }
</script>
