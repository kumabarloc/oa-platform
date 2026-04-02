<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>收文管理</span><el-button type="primary" @click="handleCreate">收文登记</el-button></div>
    </template>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width:140px">
          <el-option label="已收文" value="received" /><el-option label="分发中" value="distributing" />
          <el-option label="已处理" value="processed" /><el-option label="已归档" value="archived" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期"><el-date-picker v-model="query.startDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:150px" /></el-form-item>
      <el-form-item label="结束日期"><el-date-picker v-model="query.endDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:150px" /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="docNumber" label="收文编号" width="180" />
      <el-table-column prop="title" label="文件标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="sourceUnit" label="来文单位" width="150" />
      <el-table-column prop="receiveTime" label="收文时间" width="170" />
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }"><el-tag :type="row.priority==='high'?'danger':row.priority==='low'?'info':'warning'" size="small">{{ {high:'紧急',normal:'普通',low:'低'}[row.priority] }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }"><el-tag :type="statusTag(row.status)" size="small">{{ statusName(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button link type="warning" size="small" @click="handleEdit(row)" v-if="row.status==='received'">编辑</el-button>
          <el-button link type="success" size="small" @click="handleDistribute(row)" v-if="row.status==='received'">分发</el-button>
          <el-button link type="info" size="small" @click="handleArchive(row)" v-if="row.status==='processed'">归档</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)" v-if="row.status==='received'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-wrap"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="loadData" /></div>
  </el-card>
  <el-dialog v-model="viewVisible" title="收文详情" width="700px">
    <el-descriptions :column="2" border v-if="viewData">
      <el-descriptions-item label="收文编号">{{ viewData.docNumber }}</el-descriptions-item>
      <el-descriptions-item label="来文单位">{{ viewData.sourceUnit }}</el-descriptions-item>
      <el-descriptions-item label="收文时间">{{ viewData.receiveTime }}</el-descriptions-item>
      <el-descriptions-item label="优先级">{{ {high:'紧急',normal:'普通',low:'低'}[viewData.priority] }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ statusName(viewData.status) }}</el-descriptions-item>
      <el-descriptions-item label="文件标题" :span="2">{{ viewData.title }}</el-descriptions-item>
      <el-descriptions-item label="内容" :span="2"><div v-html="viewData.content"></div></el-descriptions-item>
    </el-descriptions>
    <template #footer><el-button @click="viewVisible=false">关闭</el-button></template>
  </el-dialog>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
      <el-form-item label="文件标题" prop="title"><el-input v-model="form.title" placeholder="请输入文件标题" /></el-form-item>
      <el-form-item label="来文单位" prop="sourceUnit"><el-input v-model="form.sourceUnit" placeholder="请输入来文单位" /></el-form-item>
      <el-form-item label="优先级"><el-select v-model="form.priority" style="width:100%"><el-option label="紧急" value="high" /><el-option label="普通" value="normal" /><el-option label="低" value="low" /></el-select></el-form-item>
      <el-form-item label="文件内容"><el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入文件内容概述" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button></template>
  </el-dialog>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getIncomingList, getIncomingDetail, createIncoming, updateIncoming, deleteIncoming, distributeIncoming, archiveIncoming } from '@/api/workflow/incoming'
const loading = ref(false), tableData = ref([]), total = ref(0)
const query = reactive({ status:'', startDate:'', endDate:'', pageNum:1, pageSize:10 })
const dialogVisible = ref(false), dialogTitle = ref('收文登记'), submitting = ref(false), formRef = ref()
const form = reactive({ id:null, title:'', sourceUnit:'', priority:'normal', content:'' })
const formRules = { title:[{required:true,message:'请输入文件标题',trigger:'blur'}], sourceUnit:[{required:true,message:'请输入来文单位',trigger:'blur'}] }
const viewVisible = ref(false), viewData = ref({})
function statusName(s){return {received:'已收文',distributing:'分发中',processed:'已处理',archived:'已归档'}[s]||s}
function statusTag(s){return {received:'primary',distributing:'warning',processed:'success',archived:'info'}[s]||''}
async function loadData(){loading.value=true;try{const res=await getIncomingList(query);tableData.value=res.list||[];total.value=res.total||0}catch{}finally{loading.value=false}}
function resetQuery(){Object.assign(query,{status:'',startDate:'',endDate:'',pageNum:1});loadData()}
function handleCreate(){dialogTitle.value='收文登记';Object.assign(form,{id:null,title:'',sourceUnit:'',priority:'normal',content:''});dialogVisible.value=true}
async function handleEdit(row){const res=await getIncomingDetail(row.id);dialogTitle.value='编辑收文';Object.assign(form,{id:res.id,title:res.title,sourceUnit:res.sourceUnit,priority:res.priority||'normal',content:res.content||''});dialogVisible.value=true}
async function handleView(row){viewData.value=await getIncomingDetail(row.id);viewVisible.value=true}
async function handleSubmit(){const valid=await formRef.value.validate().catch(()=>false);if(!valid)return;submitting.value=true;try{if(form.id){await updateIncoming(form.id,form);ElMessage.success('更新成功')}else{await createIncoming(form);ElMessage.success('收文登记成功')}dialogVisible.value=false;loadData()}catch{}finally{submitting.value=false}}
async function handleDistribute(row){await ElMessageBox.confirm(`确认分发「${row.title}」？`);await distributeIncoming(row.id);ElMessage.success('已提交分发');loadData()}
async function handleArchive(row){await ElMessageBox.confirm(`确认归档「${row.title}」？`);await archiveIncoming(row.id);ElMessage.success('已归档');loadData()}
async function handleDelete(row){await ElMessageBox.confirm(`确认删除「${row.title}」？`,'警告',{type:'warning'});await deleteIncoming(row.id);ElMessage.success('删除成功');loadData()}
onMounted(loadData)
</script>
<style scoped>.card-header{display:flex;justify-content:space-between;align-items:center}.search-bar{margin-bottom:16px}.pagination-wrap{display:flex;justify-content:flex-end;margin-top:16px}</style>
