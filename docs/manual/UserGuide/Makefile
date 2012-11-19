# Usage:
#   make -f <this-file> [<target>]
#
# Public Targets:
# * clean: removes "built" files.
# * doc-dvi: creates the documentation as a DVI document.
# * doc-pdf: creates the documentation as a PDF document.
# * doc-ps: creates the documentation as a PS document.
#
# NOTE: The 2-run of $(LATEX) after $(BIBTEX) is for the construction of
#       bibliography and bib-cross-referencing.

.PHONY: all clean doc-dvi doc-pdf doc-ps images-eps images-clean

#PKGPATH='TEXINPUTS=".//:$TEXINPUTS"' #FIXME
LATEX=latex
BIBTEX=bibtex
#PDFLATEX=pdflatex -output-format pdf
#DVIPDF=dvipdf -dCompatibility1.4
#DVIPDFM=dvipdfm
DVIPS=dvips
PDFLATEX=pdflatex
PS2PDF=ps2pdf14
DOXYGEN=doxygen
CLEANER=rm -rf

docname=UserGuide
## DUPLEX settings
##  duplex: make duplex postscript (good for draft)
##  noduplex: make one-side postscript (good for final version)
#duplexfile=./duplex
duplexfile=./noduplex
incpath=./inc

all: doc-pdf_pdflatex

doc-dvi: images-eps
	$(LATEX) $(docname)
	$(BIBTEX) $(docname)
	$(LATEX) $(docname)
	$(LATEX) $(docname)

doc-ps: doc-dvi
	$(DVIPS) -o $(docname).ps $(docname).dvi
	#$(DVIPS) -h $(duplexfile) -o $(docname).ps $(docname).dvi
	##$(DVIPS) -t landscape -o $(docname).ps $(docname).dvi

doc-pdf_dvi: doc-dvi
	$(DVIPS) -h $(duplexfile) -f < $(docname).dvi | $(PS2PDF) - $(docname).pdf

doc-pdf_pdflatex:
	$(PDFLATEX) $(docname)
	$(BIBTEX) $(docname)
	$(PDFLATEX) $(docname)
	$(PDFLATEX) $(docname)

doc-clean:
	$(CLEANER) $(docname).aux \
		$(docname).dvi \
		$(docname).ps \
		$(docname).pdf \
		$(docname).log \
		$(docname).bbl \
		$(docname).blg \
		$(docname).out \
		$(docname).toc \
		$(incpath)/*.aux \
		$(incpath)/*.dvi \
		$(incpath)/*.ps \
		$(incpath)/*.pdf \
		$(incpath)/*.log \
		$(incpath)/*.bbl \
		$(incpath)/*.blg \
		$(incpath)/*.out \
		$(incpath)/*.toc

#images-eps:
#	$(MAKE) -C ./images/dynamic eps

images-clean:
#	$(MAKE) -C ./images/dynamic clean

clean: doc-clean images-clean
