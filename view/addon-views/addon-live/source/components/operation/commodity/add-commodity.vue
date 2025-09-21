<script lang="ts" setup>
import { ref, reactive } from 'vue'

const searchModel = reactive({
  keyword: '',
})
const commodityList = ref([{ id: 1 }, { id: 2 }])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})
const sizeChange = (size: number) => {
  pagination.pageSize = size
}
const currentChange = (currentPage: number) => {
  pagination.page = currentPage
}
</script>

<template>
  <el-form :model="searchModel" inline>
    <el-form-item>
      <el-input v-model="searchModel.keyword" placeholder="请输入关键词" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary">搜索</el-button>
    </el-form-item>
  </el-form>
  <el-table :data="commodityList">
    <el-table-column fixed="left" type="selection" width="55" />
    <el-table-column label="商品名称">
      <template #default="{ row }">
        <div class="table-info">
          <img src="" />
          <div class="table-info__content">
            <span class="title">{{ row.name || '商品民称商品民称商品民称商品民称商品民称商品民称' }}</span>
            <span class="price">
              <span class="currency">￥</span>
              <span>200.09- 210.00</span>
            </span>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="库存" prop="count" width="120"></el-table-column>
  </el-table>
  <div class="pagination">
    <el-pagination
      :current-page="pagination.page"
      :page-size="pagination.pageSize"
      :page-sizes="[10, 20, 30, 40]"
      :total="pagination.total"
      layout="total, prev, pager, next, sizes"
      @size-change="sizeChange"
      @current-change="currentChange"
    />
  </div>
</template>

<style lang="scss" scoped>
@include b(table-info) {
  display: flex;
  overflow: hidden;
  img {
    width: 70px;
    height: 70px;
    border-radius: 10px;
  }
  @include e(content) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin-left: 12px;
    .title {
      text-overflow: -o-ellipsis-lastline;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      font-size: 14px;
      line-height: 20px;
    }
    .price {
      color: #f00;
      font-weight: 600;

      .currency {
        font-size: 1.2em;
      }
    }
  }
}

@include b(pagination) {
  text-align: right;
  padding-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>
