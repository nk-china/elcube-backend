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
  <span v-if="!editMode">
    <template v-if="display&&display[0]&&display[1]">
      {{display[0] | nkDatetimeISO(inputOptions.format||'YYYY/MM/DD')}}
      -
      {{display[1] | nkDatetimeISO(inputOptions.format||'YYYY/MM/DD')}}
    </template>
    <span v-else class="empty"></span>
  </span>
  <a-range-picker size="small" v-else v-model="val" @change="change" :disabled-date="disabledDate"></a-range-picker>
</template>

<script>
import moment from 'moment';
import NkFormat from 'NkFormat';
export default {
  props:{
    value: {},
    editMode: Boolean,
    inputOptions: {
      type:Object,
      default(){
        return {}
      }
    }
  },
  computed:{
    display(){
      if(this.value&&this.value[0]&&this.value[1]){
        return this.value.map(val=>{
          if(typeof val !== 'number'){
            val = moment(val, this.inputOptions.formatStorage).valueOf();
          }else if(!this.inputOptions.formatStorage){
            val = val * 1000;
          }
          return val;
        });
      }
    },
    val:{
      get(){
        if(this.value&&this.value[0]&&this.value[1]){
          if(this.inputOptions.formatStorage){
            if(typeof this.value[0] === 'number'){
              const v = this.value.map(val=>{
                return moment(val);
              });
              this.$emit('input',v.map(vv=>vv.format(this.inputOptions.formatStorage)));
              return v;
            }
            return this.value.map(val=>moment(val, this.inputOptions.formatStorage));
          }else{
            return this.value.map(val=>moment(val*1000));
          }
        }
        return undefined;
      },
      set(value){
        if(this.inputOptions.formatStorage){
          value = value && value.map(val=>{
            return val && val.format(this.inputOptions.formatStorage);
          });
          this.$emit('input',value);
        }else{
          value = value && value.map(val=>{
            return val && val.startOf('day').unix();
          });
          this.$emit('input',value);
        }
      }
    }
  },
  methods:{
    moment,
    change(){
      this.$emit('change',{});
    },
    disabledDate(current) {
      if(this.inputOptions.rangeObject){
        const unix = current.unix();
        if(this.inputOptions.rangeObject[0]!==undefined&&this.inputOptions.rangeObject[0]!==null){
          if(unix < this.inputOptions.rangeObject[0]){
            return true;
          }
        }
        if(this.inputOptions.rangeObject[1]!==undefined&&this.inputOptions.rangeObject[1]!==null){
          if(unix > this.inputOptions.rangeObject[1]){
            return true;
          }
        }
      }
      return false;
    },
  }
}
</script>

<style scoped>

</style>