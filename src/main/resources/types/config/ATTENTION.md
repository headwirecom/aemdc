## Attention

Any folder in the resources that are added to a Jar file and later read out with a Class Loader
that **contain a DOT** are not understood as name but rather as sub folder.

So **types/config/aemdc.files/aemdc-config.properties** have to be found with this path:

    **types/config/aemdc/files/aemdc-config.properties**

It would be saver to use a good package name instead like:

    **types/config/aemdcFiles/aemdc-config.properties**

Andreas Schaefer, 1/19/2017