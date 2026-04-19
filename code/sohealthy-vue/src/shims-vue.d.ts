import { ComponentPublicInstance } from 'vue';

declare module 'vue' {
    interface ComponentPublicInstance {
        isAuth: (permissions: string[]) => boolean;
    }
}
