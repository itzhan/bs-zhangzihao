<script setup lang="ts">
import { NIcon } from 'naive-ui'
import { LeafOutline, CallOutline, MailOutline, LocationOutline } from '@vicons/ionicons5'
</script>

<template>
  <footer class="footer">
    <div class="footer-inner container">
      <div class="footer-grid">
        <!-- Brand -->
        <div class="footer-brand">
          <div class="footer-logo">
            <NIcon :size="24" color="#5B8C5A">
              <LeafOutline />
            </NIcon>
            <span>农家乐预约平台</span>
          </div>
          <p class="footer-desc">发现田园之美，预约美好时光。为您精选优质农家乐，提供便捷的在线预约服务。</p>
        </div>

        <!-- Quick Links -->
        <div class="footer-links">
          <h4>快速导航</h4>
          <ul>
            <li><router-link to="/farmhouses">农家乐列表</router-link></li>
            <li><router-link to="/announcements">公告信息</router-link></li>
            <li><router-link to="/my/reservations">我的预约</router-link></li>
            <li><router-link to="/my/orders">我的订单</router-link></li>
          </ul>
        </div>

        <!-- Contact -->
        <div class="footer-contact">
          <h4>联系我们</h4>
          <div class="contact-item">
            <NIcon :size="16"><CallOutline /></NIcon>
            <span>400-888-0000</span>
          </div>
          <div class="contact-item">
            <NIcon :size="16"><MailOutline /></NIcon>
            <span>service@farmstay.com</span>
          </div>
          <div class="contact-item">
            <NIcon :size="16"><LocationOutline /></NIcon>
            <span>河南省郑州市</span>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; {{ new Date().getFullYear() }} 农家乐线上预约管理系统. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background: #2C3E2D;
  color: #c8d4c8;
  margin-top: auto;
  padding: 48px 0 0;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 48px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
}

.footer-desc {
  font-size: 14px;
  line-height: 1.7;
  color: #9CAF9C;
}

.footer-links h4,
.footer-contact h4 {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 16px;
}

.footer-links ul {
  list-style: none;
  padding: 0;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #9CAF9C;
  font-size: 14px;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: #fff;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  margin-bottom: 10px;
  color: #9CAF9C;
}

.footer-bottom {
  text-align: center;
  padding: 20px 0;
  font-size: 13px;
  color: #6B7D6B;
}

@media (max-width: 768px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}
</style>
