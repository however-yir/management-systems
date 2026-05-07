import { handleRouteGuard } from '@/router/guard';

describe('route guard', () => {
    test('allows login route without checking session', () => {
        const next = jest.fn();
        const initMenu = jest.fn();

        handleRouteGuard(
            { path: '/' },
            next,
            {
                sessionStorage: { getItem: jest.fn() },
                initMenu,
                router: {},
                store: {}
            }
        );

        expect(next).toHaveBeenCalledWith();
        expect(initMenu).not.toHaveBeenCalled();
    });

    test('initializes menu and continues when session user exists', () => {
        const next = jest.fn();
        const initMenu = jest.fn();
        const router = {};
        const store = {};

        handleRouteGuard(
            { path: '/home' },
            next,
            {
                sessionStorage: { getItem: jest.fn().mockReturnValue('{"username":"admin"}') },
                initMenu,
                router,
                store
            }
        );

        expect(initMenu).toHaveBeenCalledWith(router, store);
        expect(next).toHaveBeenCalledWith();
    });

    test('redirects to login when session user missing', () => {
        const next = jest.fn();

        handleRouteGuard(
            { path: '/emp/basic' },
            next,
            {
                sessionStorage: { getItem: jest.fn().mockReturnValue(null) },
                initMenu: jest.fn(),
                router: {},
                store: {}
            }
        );

        expect(next).toHaveBeenCalledWith('/?redirect=/emp/basic');
    });
});
