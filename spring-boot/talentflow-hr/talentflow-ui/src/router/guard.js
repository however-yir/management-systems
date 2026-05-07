export const handleRouteGuard = (to, next, deps) => {
    if (to.path === '/') {
        next();
        return;
    }

    if (deps.sessionStorage.getItem("user")) {
        deps.initMenu(deps.router, deps.store);
        next();
        return;
    }

    next('/?redirect=' + to.path);
};
