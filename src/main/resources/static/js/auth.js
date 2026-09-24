const Auth = {
  checkAuth(requiredRole) {
    const user = API.getUser();
    const token = API.getToken();

    if (!user || !token) {
      window.location.href = '/index.html';
      return null;
    }

    if (requiredRole && user.role !== requiredRole && user.role !== 'ADMIN') {
      alert(`Access denied. Role ${requiredRole} required.`);
      this.redirectByRole(user.role);
      return null;
    }

    this.renderNavbarUser(user);
    this.initNotifications();
    return user;
  },

  redirectByRole(role) {
    switch (role) {
      case 'STUDENT':
        window.location.href = '/student/dashboard.html';
        break;
      case 'HOD':
        window.location.href = '/hod/dashboard.html';
        break;
      case 'SECURITY':
        window.location.href = '/security/terminal.html';
        break;
      case 'ADMIN':
        window.location.href = '/admin/dashboard.html';
        break;
      default:
        window.location.href = '/index.html';
    }
  },

  renderNavbarUser(user) {
    const userDisplayEl = document.getElementById('navUserDisplay');
    if (userDisplayEl) {
      userDisplayEl.textContent = `${user.username} (${user.role}${user.departmentName ? ' - ' + user.departmentName : ''})`;
    }
  },

  async initNotifications() {
    const notifContainer = document.getElementById('navNotificationsList');
    const badgeEl = document.getElementById('navNotifBadge');
    if (!notifContainer) return;

    try {
      const res = await API.get('/api/v1/notifications');
      const { notifications, unreadCount } = res.data;

      if (badgeEl) {
        badgeEl.textContent = unreadCount;
        badgeEl.style.display = unreadCount > 0 ? 'inline-block' : 'none';
      }

      if (notifications.length === 0) {
        notifContainer.innerHTML = '<li><span class="dropdown-item text-muted">No notifications</span></li>';
        return;
      }

      notifContainer.innerHTML = notifications.slice(0, 6).map(n => `
        <li>
          <a class="dropdown-item ${n.read ? 'text-muted' : 'fw-bold'}" href="javascript:void(0)" onclick="Auth.markNotifRead(${n.id})">
            <small class="d-block text-secondary">${new Date(n.createdAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</small>
            <div>${n.title}</div>
            <div class="small fw-normal text-truncate" style="max-width: 280px;">${n.message}</div>
          </a>
        </li>
      `).join('') + `
        <li><hr class="dropdown-divider"></li>
        <li><a class="dropdown-item text-center small text-primary" href="javascript:void(0)" onclick="Auth.markAllNotifsRead()">Mark all as read</a></li>
      `;
    } catch (e) {
      console.warn('Failed to load notifications', e);
    }
  },

  async markNotifRead(id) {
    try {
      await API.patch(`/api/v1/notifications/${id}/read`);
      this.initNotifications();
    } catch (e) {
      console.error(e);
    }
  },

  async markAllNotifsRead() {
    try {
      await API.patch('/api/v1/notifications/read-all');
      this.initNotifications();
    } catch (e) {
      console.error(e);
    }
  },

  logout() {
    API.clearAuth();
    window.location.href = '/index.html';
  }
};
