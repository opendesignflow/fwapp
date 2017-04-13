

ci-doc:
	@make -C doc html
	@mkdir -p /data/cd/org.odfi.wsb.fwapp/fwapp-core/${BRANCH_NAME}/doc
	@cp -Rf doc/build/html/* /data/cd/org.odfi.wsb.fwapp/fwapp-core/${BRANCH_NAME}/doc/