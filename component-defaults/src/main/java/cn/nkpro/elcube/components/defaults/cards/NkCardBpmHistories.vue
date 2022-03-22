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
        <vxe-table
                ref="xTable"
                auto-resize
                keep-source
                resizable
                highlight-hover-row
                show-header-overflow="tooltip"
                show-overflow="tooltip"
                size="mini"
                border=inner
                :data="list"
                :loading="loading"
                @current-change="currentChange"
                :row-config="{
                    isCurrent:true
                }"
                :expand-config="{
                    lazy:true,
                    loadMethod:loadDetail,
                    visibleMethod
                }"

        >
            <vxe-table-column field="processDefinitionName" title="流程名称" width="40%" />
            <vxe-table-column field="startTime" title="开始时间" width="25%" formatter="nkDatetimeFriendly"  />
            <vxe-table-column field="endTime" title="结束时间" width="25%" formatter="nkDatetimeFriendly"  />

            <vxe-column type="expand">
                <template #content="{ row }">
                    <nk-bpm-timeline :histories="row.bpmTask" :task="row.activeTask" style="margin: 20px 10px 0;">
                        <span slot="assignee" v-if="row.activeTask" style="display: flex;flex-wrap:wrap;margin: 16px 10px 0;">
                            <a-comment v-for="item in row.activeTask.users" :key="item.id">
                                <a-avatar slot="avatar" size="small" style="color: #f56a00; background-color: #fde3cf;">
                                    {{ item.realname.length > 2 ? item.realname.substring(0,1) : item.realname }}
                                </a-avatar>
                                <span slot="author" style="line-height: 24px;">
                                    {{item.realname}}
                                </span>
                            </a-comment>
                        </span>
                    </nk-bpm-timeline>
                </template>
            </vxe-column>
        </vxe-table>
    </nk-card>
</template>

<script>
    import Mixin from "Mixin";

    export default {
        mixins:[new Mixin()],
        data(){
            return {
                list:undefined,
                loading:true
            }
        },
        created() {
            this.nk$call()
                .then(data=>{
                    this.list = data;
                    this.loading = false;
                }).catch(res=>{
                    console.log(res)
                })
        },
        methods:{
            currentChange({row}){
                this.$refs.xTable.setRowExpand(row, true);
            },
            loadDetail({row}){
                return new Promise((resolve,reject)=>{
                    this.nk$call(row.id)
                        .then(data=>{
                            this.$set(row,'bpmTask',data);

                            const task = data[data.length-1];
                            if(task && !task.endTime){
                                console.log(task)
                                this.$set(row,'activeTask',task);
                            }


                            resolve();
                        }).catch(reject)
                });
            },
            visibleMethod(){
                return true
            }
        }
    }
</script>

<style scoped>

</style>