package org.to2mbn.maptranslator.impl.ui;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.to2mbn.maptranslator.impl.ui.UIUtils.translate;
import java.util.ArrayList;
import java.util.List;
import org.to2mbn.maptranslator.impl.model.ParsingWarning;
import org.to2mbn.maptranslator.impl.model.ResolveFailedWarning;
import org.to2mbn.maptranslator.impl.model.StringMismatchWarning;
import com.sksamuel.diffpatch.DiffMatchPatch;
import com.sksamuel.diffpatch.DiffMatchPatch.Diff;

class ParsingResultGenerator {

	private static DiffMatchPatch differ = new DiffMatchPatch();

	public static String warningsToHtml(List<ParsingWarning> warnings) {
		List<ResolveFailedWarning> resolveFailures = new ArrayList<>();
		List<StringMismatchWarning> stringMismatches = new ArrayList<>();
		warnings.forEach(element -> {
			if (element instanceof ResolveFailedWarning)
				resolveFailures.add((ResolveFailedWarning) element);
			else if (element instanceof StringMismatchWarning)
				stringMismatches.add((StringMismatchWarning) element);
		});
// @formatter:off
		int idx=0;
		StringBuilder sb = new StringBuilder();
		sb.append(
"<!DOCTYPE html>\n" +
"<html lang=\"").append(translate("report.lang")).append("\">\n" +
"	<head>\n" +
"		<meta charset=\"utf-8\"/>\n" +
"		<title>").append(translate("report.title")).append("</title>\n" +
"		<link href=\"https://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap.min.css\" rel=\"stylesheet\"/>\n" +
"		<style type=\"text/css\">\n"+
"			.normal-hyperlink{\n"+
"				color:rgb(51,51,51) !important;\n"+
"				text-decoration:none;\n"+
"			}\n"+
"			.diff-ins{\n"+
"				background:#6fff6f;\n"+
"			}\n"+
"			.diff-del{\n"+
"				background:#ff8080;\n"+
"			}\n"+
"		</style>\n"+
"	</head>\n" +
"	<body>\n" +
"		<div class=\"container\">\n");
		if (!resolveFailures.isEmpty()) {
			sb.append(
"			<div>\n" +
"				<h2 class=\"page-header\">").append(translate("report.resolve_failure.title")).append("</h2>\n" +
"				<div>\n");
			for(ResolveFailedWarning failure:resolveFailures){
				idx++;
				sb.append(
"					<div class=\"panel panel-default\">\n" +
"						<div class=\"panel-heading\">").append(escapeHtml4(failure.path)).append("</div>\n" +
"						<div class=\"panel-body\">\n");
				sb.append(
"							<div>\n" +
"								<label>").append(translate("report.resolve_failure.command")).append("</label>\n" +
"								<pre><code>").append(escapeHtml4(failure.text)).append("</code></pre>\n" +
"							</div>\n");
				if(failure.arguments!=null){
					sb.append(
"							<div>\n" +
"								<label>").append(translate("report.resolve_failure.arguments")).append("</label>\n" +
"								<table class=\"table table-condensed table-hover\">\n");
					failure.arguments.forEach((k,v)->sb.append(
"									<tr>\n" +
"										<td>").append(escapeHtml4(k)).append("</td>\n" +
"										<td><code>").append(escapeHtml4(v)).append("</code></td>\n" +
"									</tr>\n" 
						));
					sb.append(
"								</table>\n" +
"							</div>\n");
				}
				String msg=failure.exception.getDisplayMessage();
				if(msg!=null){
					sb.append(
"							<div>\n" +
"								<label>").append(translate("report.resolve_failure.error_message")).append("</label>\n" +
"								<pre><code>").append(escapeHtml4(msg)).append("</code></pre>\n" +
"							</div>\n");
				}
				sb.append(
"							<div>\n" +
"								<label><a class=\"normal-hyperlink\" data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse").append(idx).append("\"><span class=\"icon-collapse").append(idx).append(" glyphicon glyphicon-chevron-down\" aria-hidden=\"true\"></span> ").append(translate("report.resolve_failure.stacktrace")).append("</a></label><br/>\n" +
"								<pre id=\"collapse").append(idx).append("\" class=\"collapse\" data-relatedicon=\"").append(idx).append("\"><code>").append(escapeHtml4(failure.exception.stacktrace)).append("</code></pre>\n" +
"							</div>\n" +
"						</div>\n" +
"					</div>\n");
			};
			sb.append(
"				</div>\n" + 
"			</div>\n");
		}
		if(!stringMismatches.isEmpty()){
			sb.append(
"			<div>\n" + 
"				<h2 class=\"page-header\">").append(translate("report.string_mismatch.title")).append("</h2>\n" + 
"				<div>\n");
			for(StringMismatchWarning mismatch:stringMismatches){
				sb.append(
"					<div class=\"panel panel-default\">\n" + 
"						<div class=\"panel-heading\">").append(escapeHtml4(mismatch.path)).append("</div>\n" + 
"						<div class=\"panel-body\">\n" + 
"							<div>\n" + 
"								<label>").append(translate("report.string_mismatch.comparing")).append("</label><br/>\n" + 
"								<pre><code>").append(diff_prettyHtml(differ.diff_main(mismatch.origin, mismatch.current))).append("</code></pre>\n"+
"							</div>\n" + 
"						</div>\n" + 
"					</div>\n");
			}
			sb.append(
"				</div>\n" + 
"			</div>\n");
		}
		sb.append(
"		</div>\n" +
"		<script src=\"https://cdn.bootcss.com/jquery/3.1.1/jquery.min.js\"></script>\n" + 
"		<script src=\"https://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js\"></script>\n" + 
"		<script>\n"+
"			function setCollapseIcon($this,status){\n"+
"				var element=$('.icon-collapse'+$this.data('relatedicon'));\n"+
"				if(status){\n"+
"					element.addClass('glyphicon-chevron-up');\n"+
"					element.removeClass('glyphicon-chevron-down');\n"+
"				}else{\n"+
"					element.addClass('glyphicon-chevron-down');\n"+
"					element.removeClass('glyphicon-chevron-up');\n"+
"				}\n"+
"			}\n"+
"			$(function(){\n"+
"				$('.collapse').on('show.bs.collapse',function(){\n"+
"					setCollapseIcon($(this),true);\n"+
"				});\n"+
"				$('.collapse').on('hidden.bs.collapse',function(){\n"+
"					setCollapseIcon($(this),false);\n"+
"				});\n"+
"			});\n"+
"		</script>\n" + 
"	</body>\n" + 
"</html>\n");
		// @formatter:on
		return sb.toString();
	}

	private static String diff_prettyHtml(List<Diff> diffs) {
		StringBuilder html = new StringBuilder();
		for (Diff aDiff : diffs) {
			String text = escapeHtml4(aDiff.text);
			switch (aDiff.operation) {
				case INSERT:
					html.append("<span class=\"diff-ins\">").append(text)
							.append("</span>");
					break;
				case DELETE:
					html.append("<span class=\"diff-del\">").append(text)
							.append("</span>");
					break;
				case EQUAL:
					html.append("<span>").append(text).append("</span>");
					break;
			}
		}
		return html.toString();
	}

}
