const { createApp, computed } = Vue;
const { ElMessage, ElMessageBox } = ElementPlus;

// API基础地址
const API_BASE = 'http://localhost:8080/api';

const app = createApp({
    data() {
        return {
            activeTab: 'allocation',
            allocationViewMode: 'list',

            auth: {
                token: '',
                username: '',
                role: '',
                loggedIn: false
            },
            loginDialog: {
                loading: false,
                form: {
                    username: '',
                    password: ''
                }
            },
            
            // 数据列表
            allocations: [],
            resources: [],
            projects: [],
            categories: [],
            users: [],
            scheduleItems: [],
            scheduleQuery: {
                resourceId: '',
                startDate: '',
                startTime: '',
                endDate: '',
                endTime: ''
            },
            maintenanceWindows: [],
            maintenanceFilter: {
                resourceId: '',
                startDate: '',
                startTime: '',
                endDate: '',
                endTime: ''
            },
            
            // 冲突警告
            conflictWarning: false,
            conflictMessage: '',
            
            // 资源分配对话框
            allocationDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    id: null,
                    resourceId: '',
                    projectId: '',
                    startDate: '',
                    startTime: '',
                    endDate: '',
                    endTime: '',
                    remark: ''
                }
            },
            
            // 资源对话框
            resourceDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    id: null,
                    name: '',
                    categoryId: '',
                    status: 'AVAILABLE',
                    description: ''
                }
            },
            
            // 项目对话框
            projectDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    id: null,
                    name: '',
                    manager: '',
                    status: 'ACTIVE',
                    description: ''
                }
            },
            
            // 类别对话框
            categoryDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    id: null,
                    name: '',
                    description: ''
                }
            },

            // 用户对话框
            userDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    username: '',
                    password: '',
                    role: 'USER',
                    status: 'ACTIVE'
                }
            },

            // 维护窗口对话框
            maintenanceDialog: {
                visible: false,
                title: '',
                mode: 'create',
                form: {
                    id: null,
                    resourceId: '',
                    startDate: '',
                    startTime: '',
                    endDate: '',
                    endTime: '',
                    type: 'HARD',
                    reason: ''
                }
            }
        };
    },
    
    computed: {
        getPageTitle() {
            const titles = {
                'allocation': '资源分配',
                'resource': '资源管理',
                'project': '项目管理',
                'category': '类别管理',
                'user': '用户管理',
                'maintenance': '维护窗口'
            };
            return titles[this.activeTab] || '资产调度系统';
        },

        isAdmin() {
            return this.auth.role === 'ADMIN';
        },

        scheduleAxis() {
            const range = this.getScheduleRangeMs();
            if (!range) {
                return { start: '', mid: '', end: '' };
            }
            const start = new Date(range.startMs);
            const end = new Date(range.endMs);
            const mid = new Date((range.startMs + range.endMs) / 2);
            return {
                start: this.formatDateTimeShort(start),
                mid: this.formatDateTimeShort(mid),
                end: this.formatDateTimeShort(end)
            };
        }
    },
    
    mounted() {
        this.setupAxiosInterceptors();
        this.restoreAuth();
    },
    
    methods: {
        setupAxiosInterceptors() {
            if (this._axiosInterceptorId !== undefined) {
                return;
            }
            this._axiosInterceptorId = axios.interceptors.response.use(
                response => response,
                error => {
                    if (error.response?.status === 401) {
                        this.handleUnauthorized();
                    }
                    return Promise.reject(error);
                }
            );
        },

        async restoreAuth() {
            const token = localStorage.getItem('authToken');
            if (!token) {
                this.clearAuth(false);
                return;
            }
            this.applyToken(token, false);
            try {
                const response = await axios.get(`${API_BASE}/auth/me`);
                if (response.data.code === 200) {
                    this.setAuthUser(response.data.data);
                    await this.loadAllData();
                } else {
                    this.clearAuth(false);
                }
            } catch (error) {
                this.clearAuth(false);
            }
        },

        async login() {
            const form = this.loginDialog.form;
            const username = form.username ? form.username.trim() : '';
            const password = form.password || '';
            if (!username || !password) {
                ElMessage.warning('请输入用户名和密码');
                return;
            }
            if (this.loginDialog.loading) {
                return;
            }
            this.loginDialog.loading = true;
            try {
                const response = await axios.post(`${API_BASE}/auth/login`, {
                    username,
                    password
                });
                if (response.data.code === 200) {
                    const data = response.data.data;
                    this.applyToken(data.token, true);
                    this.setAuthUser({ username: data.username, role: data.role });
                    form.password = '';
                    await this.loadAllData();
                } else {
                    ElMessage.error(response.data.message || '登录失败');
                }
            } catch (error) {
                ElMessage.error('登录失败：' + (error.response?.data?.message || error.message));
            } finally {
                this.loginDialog.loading = false;
            }
        },

        async logout() {
            try {
                await axios.post(`${API_BASE}/auth/logout`);
            } catch (error) {
                // Ignore logout errors and clear session locally.
            }
            this.clearAuth(true);
        },

        applyToken(token, persist) {
            this.auth.token = token;
            axios.defaults.headers.common.Authorization = `Bearer ${token}`;
            if (persist) {
                localStorage.setItem('authToken', token);
            }
        },

        setAuthUser(user) {
            this.auth.loggedIn = true;
            this.auth.username = user.username || '';
            this.auth.role = user.role || '';
            this.ensureActiveTab();
        },

        clearAuth(showMessage) {
            this.auth.token = '';
            this.auth.username = '';
            this.auth.role = '';
            this.auth.loggedIn = false;
            this.loginDialog.loading = false;
            this.loginDialog.form.password = '';
            this.users = [];
            delete axios.defaults.headers.common.Authorization;
            localStorage.removeItem('authToken');
            if (showMessage) {
                ElMessage.info('已退出登录');
            }
        },

        handleUnauthorized() {
            if (this.auth.loggedIn) {
                ElMessage.warning('登录已失效，请重新登录');
            }
            this.clearAuth(false);
        },

        ensureActiveTab() {
            if (!this.isAdmin && this.activeTab !== 'allocation') {
                this.activeTab = 'allocation';
            }
        },

        // 加载所有数据
        async loadAllData() {
            // Parallel loading for better performance
            const tasks = [
                this.loadCategories(),
                this.loadResources(),
                this.loadProjects(),
                this.loadAllocations()
            ];
            if (this.isAdmin) {
                tasks.push(this.loadUsers());
            } else {
                this.users = [];
            }
            await Promise.all(tasks);

            if (this.resources.length > 0) {
                if (!this.scheduleQuery.resourceId) {
                    this.scheduleQuery.resourceId = String(this.resources[0].id);
                }
                await this.loadMaintenanceWindows();
            }
        },
        
        // ========== 资源分配相关 ==========
        async loadAllocations() {
            try {
                const response = await axios.get(`${API_BASE}/allocations`);
                if (response.data.code === 200) {
                    this.allocations = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载分配列表失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        showAllocationDialog(mode, row = null) {
            this.conflictWarning = false;
            this.conflictMessage = '';
            this.allocationDialog.mode = mode;
            this.allocationDialog.title = mode === 'create' ? '新建资源分配' : '编辑资源分配';
            
            if (mode === 'create') {
                this.allocationDialog.form = {
                    id: null,
                    resourceId: '',
                    projectId: '',
                    startDate: '',
                    startTime: '',
                    endDate: '',
                    endTime: '',
                    remark: ''
                };
            } else {
                this.allocationDialog.form = {
                    id: row.id,
                    resourceId: row.resourceId != null ? String(row.resourceId) : '',
                    projectId: row.projectId != null ? String(row.projectId) : '',
                    ...this.splitDateTime(row.startTime, row.endTime),
                    remark: row.remark || ''
                };
            }
            
            this.allocationDialog.visible = true;
        },
        
        async saveAllocation() {
            const form = this.allocationDialog.form;
            const startTime = this.combineDateTime(form.startDate, form.startTime);
            const endTime = this.combineDateTime(form.endDate, form.endTime);
            const payload = {
                id: form.id,
                resourceId: this.normalizeId(form.resourceId),
                projectId: this.normalizeId(form.projectId),
                startTime,
                endTime,
                remark: form.remark
            };
            
            // 验证
            if (!payload.resourceId || !payload.projectId || !payload.startTime || !payload.endTime) {
                ElMessage.warning('请填写完整信息');
                return;
            }
            
            try {
                let response;
                if (this.allocationDialog.mode === 'create') {
                    response = await axios.post(`${API_BASE}/allocations`, payload);
                } else {
                    response = await axios.put(`${API_BASE}/allocations/${payload.id}`, payload);
                }
                
                if (response.data.code === 200) {
                    ElMessage.success('保存成功');
                    this.allocationDialog.visible = false;
                    await this.loadAllocations();
                } else {
                    ElMessage.error(response.data.message);
                }
            } catch (error) {
                const message = error.response?.data?.message || error.message;
                ElMessage.error(message);
            }
        },
        
        async checkConflict() {
            const form = this.allocationDialog.form;
            const startTime = this.combineDateTime(form.startDate, form.startTime);
            const endTime = this.combineDateTime(form.endDate, form.endTime);
            const payload = {
                resourceId: this.normalizeId(form.resourceId),
                startTime,
                endTime
            };
            
            if (!payload.resourceId || !payload.startTime || !payload.endTime) {
                ElMessage.warning('请先选择资源和时间段');
                return;
            }
            
            try {
                const response = await axios.post(`${API_BASE}/allocations/check-conflict`, payload);
                
                if (response.data.code === 200) {
                    const result = response.data.data;
                    this.conflictWarning = result.hasConflict;
                    this.conflictMessage = result.message;
                    
                    if (result.type === 'HARD') {
                        ElMessage.error(result.message);
                    } else if (result.type === 'SOFT') {
                        ElMessage.warning(result.message);
                    } else {
                        ElMessage.success(result.message);
                    }
                }
            } catch (error) {
                ElMessage.error('检测失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        async deleteAllocation(id) {
            try {
                await ElMessageBox.confirm('确定要删除此分配记录吗？此操作无法撤销。', '确认删除', {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消',
                    confirmButtonClass: 'el-button--danger'
                });
                
                const response = await axios.delete(`${API_BASE}/allocations/${id}`);
                if (response.data.code === 200) {
                    ElMessage.success('删除成功');
                    await this.loadAllocations();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败：' + (error.response?.data?.message || error.message));
                }
            }
        },

        async loadSchedule() {
            const resourceId = this.normalizeId(this.scheduleQuery.resourceId);
            const startTime = this.combineDateTime(
                this.scheduleQuery.startDate,
                this.scheduleQuery.startTime
            );
            const endTime = this.combineDateTime(
                this.scheduleQuery.endDate,
                this.scheduleQuery.endTime
            );

            if (!resourceId || !startTime || !endTime) {
                ElMessage.warning('请填写资源和时间范围');
                return;
            }

            try {
                const response = await axios.get(`${API_BASE}/resources/${resourceId}/schedule`, {
                    params: { startTime, endTime }
                });
                if (response.data.code === 200) {
                    this.scheduleItems = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载时间段视图失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        // ========== 资源相关 ==========
        async loadResources() {
            try {
                const response = await axios.get(`${API_BASE}/resources`);
                if (response.data.code === 200) {
                    this.resources = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载资源列表失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        showResourceDialog(mode, row = null) {
            this.resourceDialog.mode = mode;
            this.resourceDialog.title = mode === 'create' ? '新建资源' : '编辑资源';
            
            if (mode === 'create') {
                this.resourceDialog.form = {
                    id: null,
                    name: '',
                    categoryId: '',
                    status: 'AVAILABLE',
                    description: ''
                };
            } else {
                this.resourceDialog.form = {
                    id: row.id,
                    name: row.name || '',
                    categoryId: row.categoryId != null ? String(row.categoryId) : '',
                    status: row.status || 'AVAILABLE',
                    description: row.description || ''
                };
            }
            
            this.resourceDialog.visible = true;
        },
        
        async saveResource() {
            const form = this.resourceDialog.form;
            const payload = {
                id: form.id,
                name: form.name,
                categoryId: this.normalizeId(form.categoryId),
                status: form.status,
                description: form.description
            };
            
            if (!payload.name || !payload.categoryId) {
                ElMessage.warning('请填写完整信息');
                return;
            }
            
            try {
                let response;
                if (this.resourceDialog.mode === 'create') {
                    response = await axios.post(`${API_BASE}/resources`, payload);
                } else {
                    response = await axios.put(`${API_BASE}/resources/${payload.id}`, payload);
                }
                
                if (response.data.code === 200) {
                    ElMessage.success('保存成功');
                    this.resourceDialog.visible = false;
                    await this.loadResources();
                }
            } catch (error) {
                ElMessage.error('保存失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        async deleteResource(id) {
            try {
                await ElMessageBox.confirm('确定要删除此资源吗？', '确认删除', {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消'
                });
                
                const response = await axios.delete(`${API_BASE}/resources/${id}`);
                if (response.data.code === 200) {
                    ElMessage.success('删除成功');
                    await this.loadResources();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败：' + (error.response?.data?.message || error.message));
                }
            }
        },
        
        // ========== 项目相关 ==========
        async loadProjects() {
            try {
                const response = await axios.get(`${API_BASE}/projects`);
                if (response.data.code === 200) {
                    this.projects = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载项目列表失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        showProjectDialog(mode, row = null) {
            this.projectDialog.mode = mode;
            this.projectDialog.title = mode === 'create' ? '新建项目' : '编辑项目';
            
            if (mode === 'create') {
                this.projectDialog.form = {
                    id: null,
                    name: '',
                    manager: '',
                    status: 'ACTIVE',
                    description: ''
                };
            } else {
                this.projectDialog.form = { ...row };
            }
            
            this.projectDialog.visible = true;
        },
        
        async saveProject() {
            const form = this.projectDialog.form;
            
            if (!form.name) {
                ElMessage.warning('请填写项目名称');
                return;
            }
            
            try {
                let response;
                if (this.projectDialog.mode === 'create') {
                    response = await axios.post(`${API_BASE}/projects`, form);
                } else {
                    response = await axios.put(`${API_BASE}/projects/${form.id}`, form);
                }
                
                if (response.data.code === 200) {
                    ElMessage.success('保存成功');
                    this.projectDialog.visible = false;
                    await this.loadProjects();
                }
            } catch (error) {
                ElMessage.error('保存失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        async deleteProject(id) {
            try {
                await ElMessageBox.confirm('确定要删除此项目吗？', '确认删除', {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消'
                });
                
                const response = await axios.delete(`${API_BASE}/projects/${id}`);
                if (response.data.code === 200) {
                    ElMessage.success('删除成功');
                    await this.loadProjects();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败：' + (error.response?.data?.message || error.message));
                }
            }
        },
        
        // ========== 类别相关 ==========
        async loadCategories() {
            try {
                const response = await axios.get(`${API_BASE}/categories`);
                if (response.data.code === 200) {
                    this.categories = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载类别列表失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        showCategoryDialog(mode, row = null) {
            this.categoryDialog.mode = mode;
            this.categoryDialog.title = mode === 'create' ? '新建类别' : '编辑类别';
            
            if (mode === 'create') {
                this.categoryDialog.form = {
                    id: null,
                    name: '',
                    description: ''
                };
            } else {
                this.categoryDialog.form = { ...row };
            }
            
            this.categoryDialog.visible = true;
        },
        
        async saveCategory() {
            const form = this.categoryDialog.form;
            
            if (!form.name) {
                ElMessage.warning('请填写类别名称');
                return;
            }
            
            try {
                let response;
                if (this.categoryDialog.mode === 'create') {
                    response = await axios.post(`${API_BASE}/categories`, form);
                } else {
                    response = await axios.put(`${API_BASE}/categories/${form.id}`, form);
                }
                
                if (response.data.code === 200) {
                    ElMessage.success('保存成功');
                    this.categoryDialog.visible = false;
                    await this.loadCategories();
                }
            } catch (error) {
                ElMessage.error('保存失败：' + (error.response?.data?.message || error.message));
            }
        },
        
        async deleteCategory(id) {
            try {
                await ElMessageBox.confirm('确定要删除此类别吗？', '确认删除', {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消'
                });
                
                const response = await axios.delete(`${API_BASE}/categories/${id}`);
                if (response.data.code === 200) {
                    ElMessage.success('删除成功');
                    await this.loadCategories();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败：' + (error.response?.data?.message || error.message));
                }
            }
        },

        // ========== 用户相关 ==========
        async loadUsers() {
            try {
                const response = await axios.get(`${API_BASE}/users`);
                if (response.data.code === 200) {
                    this.users = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载用户列表失败：' + (error.response?.data?.message || error.message));
            }
        },

        showUserDialog() {
            this.userDialog.mode = 'create';
            this.userDialog.title = '新建用户';
            this.userDialog.form = {
                username: '',
                password: '',
                role: 'USER',
                status: 'ACTIVE'
            };
            this.userDialog.visible = true;
        },

        async saveUser() {
            const form = this.userDialog.form;
            const username = form.username ? form.username.trim() : '';
            const password = form.password || '';
            if (!username || !password) {
                ElMessage.warning('请填写用户名和密码');
                return;
            }
            const payload = {
                username,
                password,
                role: form.role,
                status: form.status
            };
            try {
                const response = await axios.post(`${API_BASE}/users`, payload);
                if (response.data.code === 200) {
                    ElMessage.success('创建成功');
                    this.userDialog.visible = false;
                    await this.loadUsers();
                } else {
                    ElMessage.error(response.data.message || '创建失败');
                }
            } catch (error) {
                ElMessage.error('创建失败：' + (error.response?.data?.message || error.message));
            }
        },

        // ========== 维护窗口相关 ==========
        async loadMaintenanceWindows() {
            const resourceId = this.normalizeId(this.maintenanceFilter.resourceId);
            const startTime = this.combineDateTime(
                this.maintenanceFilter.startDate,
                this.maintenanceFilter.startTime
            );
            const endTime = this.combineDateTime(
                this.maintenanceFilter.endDate,
                this.maintenanceFilter.endTime
            );
            const params = {};
            if (resourceId !== null) {
                params.resourceId = resourceId;
            }
            if (startTime || endTime) {
                if (!startTime || !endTime) {
                    ElMessage.warning('请填写完整时间范围');
                    return;
                }
                params.startTime = startTime;
                params.endTime = endTime;
            }

            try {
                const response = await axios.get(`${API_BASE}/maintenance-windows`, { params });
                if (response.data.code === 200) {
                    this.maintenanceWindows = response.data.data;
                }
            } catch (error) {
                ElMessage.error('加载维护窗口失败：' + (error.response?.data?.message || error.message));
            }
        },

        showMaintenanceDialog(mode, row = null) {
            this.maintenanceDialog.mode = mode;
            this.maintenanceDialog.title = mode === 'create' ? '新建维护窗口' : '编辑维护窗口';

            if (mode === 'create') {
                this.maintenanceDialog.form = {
                    id: null,
                    resourceId: this.maintenanceFilter.resourceId || '',
                    startDate: '',
                    startTime: '',
                    endDate: '',
                    endTime: '',
                    type: 'HARD',
                    reason: ''
                };
            } else {
                this.maintenanceDialog.form = {
                    id: row.id,
                    resourceId: row.resourceId != null ? String(row.resourceId) : '',
                    ...this.splitDateTime(row.startTime, row.endTime),
                    type: row.type || 'HARD',
                    reason: row.reason || ''
                };
            }

            this.maintenanceDialog.visible = true;
        },

        async saveMaintenanceWindow() {
            const form = this.maintenanceDialog.form;
            const startTime = this.combineDateTime(form.startDate, form.startTime);
            const endTime = this.combineDateTime(form.endDate, form.endTime);
            const payload = {
                id: form.id,
                resourceId: this.normalizeId(form.resourceId),
                startTime,
                endTime,
                type: form.type,
                reason: form.reason
            };

            if (!payload.resourceId || !payload.startTime || !payload.endTime) {
                ElMessage.warning('请填写完整信息');
                return;
            }

            try {
                let response;
                if (this.maintenanceDialog.mode === 'create') {
                    response = await axios.post(`${API_BASE}/maintenance-windows`, payload);
                } else {
                    response = await axios.put(`${API_BASE}/maintenance-windows/${payload.id}`, payload);
                }

                if (response.data.code === 200) {
                    ElMessage.success('保存成功');
                    this.maintenanceDialog.visible = false;
                    await this.loadMaintenanceWindows();
                }
            } catch (error) {
                ElMessage.error('保存失败：' + (error.response?.data?.message || error.message));
            }
        },

        async deleteMaintenanceWindow(id) {
            try {
                await ElMessageBox.confirm('确定要删除此维护窗口吗？', '确认删除', {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消'
                });

                const response = await axios.delete(`${API_BASE}/maintenance-windows/${id}`);
                if (response.data.code === 200) {
                    ElMessage.success('删除成功');
                    await this.loadMaintenanceWindows();
                }
            } catch (error) {
                if (error !== 'cancel') {
                    ElMessage.error('删除失败：' + (error.response?.data?.message || error.message));
                }
            }
        },
        
        // ========== 工具方法 ==========
        getResourceName(id) {
            const resource = this.resources.find(r => r.id === id);
            return resource ? resource.name : '-';
        },
        
        getProjectName(id) {
            const project = this.projects.find(p => p.id === id);
            return project ? project.name : '-';
        },
        
        getCategoryName(id) {
            const category = this.categories.find(c => c.id === id);
            return category ? category.name : '-';
        },
        
        getStatusText(status) {
            const statusMap = {
                'ACTIVE': '进行中',
                'AVAILABLE': '可用',
                'COMPLETED': '已完成',
                'CANCELLED': '已取消',
                'MAINTENANCE': '维护中',
                'RETIRED': '已退役'
            };
            return statusMap[status] || status;
        },

        getUserRoleText(role) {
            if (role === 'ADMIN') {
                return '管理员';
            }
            if (role === 'USER') {
                return '普通用户';
            }
            return role || '-';
        },

        getUserStatusText(status) {
            if (status === 'ACTIVE') {
                return '启用';
            }
            if (status === 'DISABLED') {
                return '禁用';
            }
            return status || '-';
        },

        getStatusClass(status) {
            const classMap = {
                'ACTIVE': 'success',
                'AVAILABLE': 'success',
                'COMPLETED': 'info',
                'CANCELLED': 'danger',
                'MAINTENANCE': 'warning',
                'RETIRED': 'danger'
            };
            return classMap[status] || 'info';
        },

        getScheduleTypeText(type) {
            if (type === 'ALLOCATION') {
                return '分配';
            }
            if (type === 'MAINTENANCE') {
                return '维护窗口';
            }
            return type || '-';
        },

        getScheduleLevelText(level) {
            if (level === 'HARD') {
                return '硬冲突';
            }
            if (level === 'SOFT') {
                return '软冲突';
            }
            return level || '-';
        },

        getScheduleLevelClass(level) {
            if (level === 'HARD') {
                return 'danger';
            }
            if (level === 'SOFT') {
                return 'warning';
            }
            return 'info';
        },

        getScheduleBarClass(item) {
            if (item.type === 'ALLOCATION') {
                return 'allocation';
            }
            if (item.type === 'MAINTENANCE' && item.level === 'HARD') {
                return 'maintenance-hard';
            }
            if (item.type === 'MAINTENANCE' && item.level === 'SOFT') {
                return 'maintenance-soft';
            }
            return 'allocation';
        },

        getScheduleBarText(item) {
            if (item.type === 'ALLOCATION') {
                return '分配';
            }
            if (item.type === 'MAINTENANCE') {
                return item.level === 'SOFT' ? '维护(软)' : '维护(硬)';
            }
            return '';
        },

        getScheduleRangeMs() {
            const startValue = this.combineDateTime(
                this.scheduleQuery.startDate,
                this.scheduleQuery.startTime
            );
            const endValue = this.combineDateTime(
                this.scheduleQuery.endDate,
                this.scheduleQuery.endTime
            );
            const startMs = this.parseDateTime(startValue);
            const endMs = this.parseDateTime(endValue);
            if (Number.isNaN(startMs) || Number.isNaN(endMs) || endMs <= startMs) {
                return null;
            }
            return { startMs, endMs };
        },

        getScheduleBarStyle(item) {
            const range = this.getScheduleRangeMs();
            if (!range) {
                return { display: 'none' };
            }
            const itemStart = this.parseDateTime(item.startTime);
            const itemEnd = this.parseDateTime(item.endTime);
            if (Number.isNaN(itemStart) || Number.isNaN(itemEnd)) {
                return { display: 'none' };
            }
            const total = range.endMs - range.startMs;
            if (total <= 0) {
                return { display: 'none' };
            }

            let left = ((itemStart - range.startMs) / total) * 100;
            let right = ((itemEnd - range.startMs) / total) * 100;
            if (right < 0 || left > 100) {
                return { display: 'none' };
            }

            left = Math.max(left, 0);
            right = Math.min(right, 100);
            const width = Math.max(right - left, 1);

            return {
                left: `${left}%`,
                width: `${width}%`
            };
        },

        normalizeId(value) {
            if (value === null || value === undefined || value === '') {
                return null;
            }
            const parsed = Number(value);
            return Number.isNaN(parsed) ? null : parsed;
        },

        combineDateTime(date, time) {
            if (!date || !time) {
                return '';
            }
            const normalizedTime = time.length === 5 ? `${time}:00` : time;
            return `${date}T${normalizedTime}`;
        },

        splitDateTime(startValue, endValue) {
            const start = this.splitSingleDateTime(startValue);
            const end = this.splitSingleDateTime(endValue);
            return {
                startDate: start.date,
                startTime: start.time,
                endDate: end.date,
                endTime: end.time
            };
        },

        splitSingleDateTime(value) {
            if (!value) {
                return { date: '', time: '' };
            }
            const raw = String(value).trim();
            const normalized = raw.includes(' ') ? raw.replace(' ', 'T') : raw;
            const parts = normalized.split('T');
            if (parts.length < 2) {
                return { date: '', time: '' };
            }
            const date = parts[0];
            const time = parts[1].split('.')[0].slice(0, 5);
            return { date, time };
        },

        parseDateTime(value) {
            if (!value) {
                return Number.NaN;
            }
            if (value instanceof Date) {
                return value.getTime();
            }
            const raw = String(value).trim();
            const normalized = raw.includes(' ') ? raw.replace(' ', 'T') : raw;
            const parsed = new Date(normalized);
            const ms = parsed.getTime();
            return Number.isNaN(ms) ? Number.NaN : ms;
        },

        formatDateTimeShort(value) {
            const ms = value instanceof Date ? value.getTime() : this.parseDateTime(value);
            if (Number.isNaN(ms)) {
                return '-';
            }
            return new Date(ms).toLocaleString('zh-CN', {
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        },

        formatDateTime(timeStr) {
            if (!timeStr) return '-';
            // Simple formatter, can be enhanced with dayjs if needed
            const date = new Date(timeStr);
            return date.toLocaleString('zh-CN', { 
                month: 'numeric', 
                day: 'numeric', 
                hour: '2-digit', 
                minute: '2-digit' 
            });
        }
    }
});

// Register Element Plus Icons
if (typeof ElementPlusIconsVue !== 'undefined') {
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
        app.component(key, component);
    }
} else {
    console.warn('Element Plus Icons not loaded. Check your network connection.');
}

app.use(ElementPlus);
app.mount('#app');
