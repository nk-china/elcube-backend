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
    <nk-meter ref="meter" :title="configEdit.title||title||'统计数值'" :editable="editable" :enable-setting="true" @setting-submit="update">
        <div style="height: 100%;width: 100%;">
            <a-row v-if="statistic && statistic.length" style="height: 100%;width: 100%;margin: 0;">
                <a-col :span="24/statistic.length" v-for="(item,index) in statistic" style="height: 100%;" class="statistic">
                    <a-card style="height: 100%;" :style="{'margin-left':index > 0 ? '10px':'0'}">
                        <a-statistic
                                :title="item.key"
                                :value="item.value"
                                :precision="2"
                                suffix="%"
                                :value-style="{ color: '#3f8600' }"
                        >
                            <template #prefix>
                                <a-icon type="arrow-up" />
                            </template>
                        </a-statistic>
                    </a-card>
                </a-col>
            </a-row>
        </div>
        <div slot="setting">
            <nk-form :col="1" :edit="true">
                <nk-form-item title="标题" :width="80">
                    <a-input slot="edit" v-model="configEdit.title"></a-input>
                </nk-form-item>
                <nk-form-item title="数据" :width="80" v-if="!defaultData">
                    <a-textarea slot="edit" v-model="configEdit.sql" rows="5" placeholder="ElasticSearch SQL OR JSON Data"></a-textarea>
                </nk-form-item>
                <nk-form-item title="X轴字段" :width="80">
                    <a-select v-model="configEdit.xField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="Y轴字段" :width="80">
                    <a-select v-model="configEdit.yField" :mode="columnDefs?'default':'tags'" :options="columnDefs"></a-select>
                </nk-form-item>
                <nk-form-item title="缩略轴" :width="80">
                    <a-switch v-model="configEdit.slider" size="small"></a-switch>
                </nk-form-item>
                <nk-form-item title="中位数" :width="80">
                    <a-switch v-model="configEdit.median" size="small"></a-switch>
                </nk-form-item>
            </nk-form>
        </div>
    </nk-meter>
</template>

<script>

    export default {
        props:{
            editable:Boolean,
            title:String,
            value:Object,
            defaultData:Array,
            columnDefs:Array,
        },
        data(){
            return {
                statistic:undefined,
                configEdit:undefined,
            }
        },
        created(){
            this.configEdit = Object.assign({},this.value);
        },
        mounted(){
            if(this.configEdit.sql){
                this.render(this.configEdit);
            }else{
                this.$refs.meter.setSettingMode(true);
            }
        },
        methods:{
            load(config){
                if(this.defaultData){
                    return Promise.resolve({data:this.defaultData});
                }
                if(config.sql){
                    if(config.sql.trim().startsWith("[") && config.sql.trim().endsWith("]")){
                        return new Promise((r)=>{setTimeout(()=>{r({data:JSON.parse(config.sql)});},100);});
                    }
                    return this.$http.postJSON(`/api/meter/card/get/${this.$options._componentTag}`,config);
                }
                if(config.data){
                    new Promise((r)=>{setTimeout(()=>{r({data:JSON.parse(config.data)});},100);});
                }
                return Promise.resolve({data:[]});
            },
            render(config){
                this.load(config).then(res=>{
                    if(res.data[0]){
                        console.log(res.data)
                        console.log(res.data[0])
                        console.log(Object.keys(res.data[0]))
                        this.statistic = Object.keys(res.data[0])
                            .map(key=>{
                                return {
                                    key,
                                    value: res.data[0][key]
                                }
                            })

                        console.log(this.statistic)
                    }
                });
            },
            update(){
                try{
                    this.render(this.configEdit);
                    const event = Object.assign({},this.configEdit);
                    this.$emit("update:config",event);
                    this.$emit("input",event);
                }catch (e) {
                    console.error(e);
                    this.$refs.meter.setSettingMode(true);
                }
            }
        }
    }
</script>

<style scoped lang="less">
    ::v-deep.meter{
        position: relative;
        border: none !important;
        .ant-card-body{
            height: 100% !important;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #f0f2f5;
            padding: 0 !important;

            .ant-card-body{
                background-color: #ffffff;
            }
        }
        .ant-card-head{
            display: none;
        }
        &:hover{
            .ant-card-head{
                display: block;
                position: absolute;
                z-index: 1;
                background-color: #fff;
                width: 100%;
            }
        }
    }
</style>