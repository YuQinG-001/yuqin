import type { ComponentPublicInstance } from 'vue';

// 扩展 ComponentPublicInstance 类型，添加全局属性
declare module 'vue' {
    interface ComponentCustomProperties {
        isAuth: (permissions: string[]) => boolean;
        $minioUrl: string;
        $socket: Socket;
    }
}

export {};
