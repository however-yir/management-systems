import { shallowMount } from '@vue/test-utils';
import flushPromises from 'flush-promises';
import Login from '@/views/Login.vue';

const buildWrapper = (query = {}) => {
    const commit = jest.fn();
    const replace = jest.fn();

    const wrapper = shallowMount(Login, {
        stubs: ['el-form', 'el-form-item', 'el-input', 'el-checkbox', 'el-button'],
        directives: {
            loading: jest.fn()
        },
        mocks: {
            $store: { commit },
            $router: { replace },
            $route: { query }
        }
    });

    wrapper.vm.$refs.loginForm = {
        validate: (cb) => cb(true)
    };

    return { wrapper, commit, replace };
};

describe('Login.vue', () => {
    beforeEach(() => {
        window.sessionStorage.clear();
    });

    test('stores user in vuex and sessionStorage then redirects to /home', async () => {
        const { wrapper, commit, replace } = buildWrapper();
        const response = { obj: { id: 3, username: 'admin' } };
        wrapper.vm.postRequest = jest.fn().mockResolvedValue(response);

        wrapper.vm.submitLogin();
        await flushPromises();

        expect(wrapper.vm.postRequest).toHaveBeenCalledWith('/doLogin', wrapper.vm.loginForm);
        expect(commit).toHaveBeenCalledWith('INIT_CURRENTHR', response.obj);
        expect(JSON.parse(window.sessionStorage.getItem('user'))).toEqual(response.obj);
        expect(replace).toHaveBeenCalledWith('/home');
    });

    test('uses redirect query after successful login', async () => {
        const { wrapper, replace } = buildWrapper({ redirect: '/chat' });
        wrapper.vm.postRequest = jest.fn().mockResolvedValue({ obj: { id: 3, username: 'admin' } });

        wrapper.vm.submitLogin();
        await flushPromises();

        expect(replace).toHaveBeenCalledWith('/chat');
    });

    test('refreshes verify code when backend login returns empty response', async () => {
        const { wrapper, commit, replace } = buildWrapper();
        wrapper.vm.postRequest = jest.fn().mockResolvedValue(null);

        wrapper.vm.submitLogin();
        await flushPromises();

        expect(commit).not.toHaveBeenCalled();
        expect(replace).not.toHaveBeenCalled();
        expect(wrapper.vm.vcUrl.startsWith('/verifyCode?time=')).toBe(true);
    });
});
