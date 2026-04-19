import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';

// 定义后端统一返回格式
interface R<T = any> {
    code: number;
    msg: string;
    result: T;
}

class Request {
    private axiosInstance: AxiosInstance;

    constructor(baseURL: string) {
        this.axiosInstance = axios.create({
            baseURL,
            timeout: 10000,
        });

        // 请求拦截器
        this.axiosInstance.interceptors.request.use(
            (config) => {
                // 添加token
                const token = localStorage.getItem('token');
                if (token) {
                    config.headers['satoken'] = token;
                }
                return config;
            },
            (error) => {
                return Promise.reject(error);
            },
        );

        // 响应拦截器
        this.axiosInstance.interceptors.response.use(
            (response: AxiosResponse<R>) => {
                const { code, msg, result } = response.data;
                // 业务成功
                if (code === 200) {
                    return result;
                }
                // 业务失败
                ElMessage.error(msg || '请求失败');
                return Promise.reject(new Error(msg || '请求失败'));
            },
            (error) => {
                // 网络错误或HTTP状态码错误
                if (error.response) {
                    const { status, data } = error.response;
                    let errorMsg = '';
                    switch (status) {
                        case 401:
                            errorMsg = '未登录，请重新登录';
                            // 清除token并跳转到登录页
                            localStorage.removeItem('token');
                            window.location.href = '/login';
                            break;
                        case 403:
                            errorMsg = '没有权限访问';
                            break;
                        case 500:
                            errorMsg = '服务器内部错误';
                            break;
                        default:
                            errorMsg = data?.msg || `请求失败 (状态码: ${status})`;
                    }
                    ElMessage.error(errorMsg);
                } else if (error.request) {
                    ElMessage.error('网络连接失败，请检查网络');
                } else {
                    ElMessage.error('请求配置错误');
                }
                return Promise.reject(error);
            },
        );
    }

    // 通用请求方法
    get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return this.axiosInstance.get(url, config);
    }

    post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return this.axiosInstance.post(url, data, config);
    }

    put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
        return this.axiosInstance.put(url, data, config);
    }
    
    delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
        return this.axiosInstance.delete(url, config);
    }
}

// 创建实例，根据后端配置的context-path
const request = new Request('/meinian-api');

export default request;
