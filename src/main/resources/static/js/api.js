const API = {
  baseUrl: '',

  getToken() {
    return localStorage.getItem('campuspass_token');
  },

  setAuth(authResponse) {
    localStorage.setItem('campuspass_token', authResponse.token);
    localStorage.setItem('campuspass_user', JSON.stringify(authResponse));
  },

  getUser() {
    const raw = localStorage.getItem('campuspass_user');
    return raw ? JSON.parse(raw) : null;
  },

  clearAuth() {
    localStorage.removeItem('campuspass_token');
    localStorage.removeItem('campuspass_user');
  },

  async request(endpoint, options = {}) {
    const token = this.getToken();
    const headers = options.headers || {};

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }

    options.headers = headers;

    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, options);

      if (response.status === 401) {
        if (!window.location.pathname.endsWith('index.html') && window.location.pathname !== '/') {
          this.clearAuth();
          window.location.href = '/index.html?expired=true';
          return;
        }
      }

      const json = await response.json();

      if (!response.ok || (json && json.success === false)) {
        const errorMsg = (json && json.message) || `Request failed with status ${response.status}`;
        throw new Error(errorMsg);
      }

      return json;
    } catch (err) {
      console.error(`API Error [${endpoint}]:`, err);
      throw err;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  },

  post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data)
    });
  },

  postFormData(endpoint, formData) {
    return this.request(endpoint, {
      method: 'POST',
      body: formData
    });
  },

  patch(endpoint, data = {}) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(data)
    });
  },

  put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data)
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
};
