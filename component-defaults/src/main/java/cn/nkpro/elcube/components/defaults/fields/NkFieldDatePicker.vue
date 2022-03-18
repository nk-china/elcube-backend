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
      <template v-if="display">{{display | nkDatetimeISO(inputOptions.format||(inputOptions.showTime?'YYYY/MM/DD HH:mm:ss':'YYYY/MM/DD'))}}</template>
      <span v-else class="empty"></span>
    </span>
  <a-date-picker size="small" v-else v-model="val" @change="change"
                 :show-time="inputOptions.showTime?{ defaultValue: moment('00:00:00', 'HH:mm:ss') }:false"
                 :disabled-date="disabledDate"
  ></a-date-picker>
</template>

<script>
import moment from 'moment';
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
      if(this.value){
        let val = this.value;
        if(typeof val !== 'number'){
          val = moment(this.value, this.inputOptions.formatStorage).valueOf();
        }else if(!this.inputOptions.formatStorage){
          val = val * 1000;
        }
        return val;
      }
    },
    val:{
      get(){
        if(this.value){
          if(this.inputOptions.formatStorage){
            if(typeof this.value === 'number'){
              const v = moment(this.value);
              this.$emit('input',v.format(this.inputOptions.formatStorage));
              return v;
            }
            return moment(this.value, this.inputOptions.formatStorage)
          }else{
            return moment(this.value * 1000)
          }
        }
        return undefined;
      },
      set(value){

        if(this.inputOptions.formatStorage){
          value = value && value.format(this.inputOptions.formatStorage);
          this.$emit('input',value);
        }else{
          if(this.inputOptions.showTime){
            value = value && value.unix();
            this.$emit('input',value);
          }else{
            value = value && value.startOf('day').unix();
            this.$emit('input',value);
          }
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

      try{
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
      }catch (e){
      }
      return false;
    },
  }
}
</script>

<style scoped>

</style>