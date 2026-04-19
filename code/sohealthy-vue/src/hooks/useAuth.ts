import { getCurrentInstance } from 'vue';

export default function useAuth() {
    const { proxy } = getCurrentInstance()!;
    const isAuth = (permissions: string[]) => {
        return proxy?.isAuth(permissions) ?? false;
    };
    return { isAuth };
}
