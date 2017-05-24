
ci-doc: BRANCH=$$(git rev-parse --abbrev-ref HEAD)
ci-doc:
	@echo "Making Doc for branch: $(BRANCH)"
	@make -C doc html
	@mkdir -p /data/cd/org.odfi.wsb.fwapp/fwapp-core/$(BRANCH)/doc
	@cp -Rf doc/build/html/* /data/cd/org.odfi.wsb.fwapp/fwapp-core/$(BRANCH)/doc/