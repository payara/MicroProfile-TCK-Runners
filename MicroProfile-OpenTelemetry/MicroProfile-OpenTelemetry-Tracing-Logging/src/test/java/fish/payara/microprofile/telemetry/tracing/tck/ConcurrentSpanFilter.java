/*
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://github.com/payara/Payara/blob/master/LICENSE.txt
 *  See the License for the specific
 *  language governing permissions and limitations under the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *  GPL Classpath Exception:
 *  The Payara Foundation designates this particular file as subject to the "Classpath"
 *  exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *  file that accompanied this code.
 *
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 *
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 *
 */
package fish.payara.microprofile.telemetry.tracing.tck;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

import java.util.List;

/**
 * Registers a sampler that drops Payara's Jakarta Concurrency waiting spans from the OTel
 * pipeline so that TCK tests asserting exact span counts are not disturbed by Payara's
 * managed-executor instrumentation.
 *
 * <p>Spans are identified by the {@code payara.subsystem = "jakarta-concurrency"} attribute
 * set by Payara's {@code OtelContextProvider}, which is more stable than matching on the
 * span name pattern.
 *
 * <p>Loaded via the standard {@link AutoConfigurationCustomizerProvider} service-loader SPI
 * and added to every TCK test WAR by {@link ArquillianExtension}.
 */
public class ConcurrentSpanFilter implements AutoConfigurationCustomizerProvider {

    private static final AttributeKey<String> PAYARA_SUBSYSTEM =
            AttributeKey.stringKey("payara.subsystem");

    @Override
    public void customize(AutoConfigurationCustomizer customizer) {
        customizer.addSamplerCustomizer(
                (delegate, config) -> new DroppingSampler(delegate));
    }

    /**
     * Drops spans that carry {@code payara.subsystem = "jakarta-concurrency"}.
     */
    static final class DroppingSampler implements Sampler {

        private final Sampler delegate;

        DroppingSampler(Sampler delegate) {
            this.delegate = delegate;
        }

        @Override
        public SamplingResult shouldSample(Context parentContext, String traceId, String name,
                SpanKind spanKind, Attributes attributes, List<LinkData> links) {
            if ("jakarta-concurrency".equals(attributes.get(PAYARA_SUBSYSTEM))) {
                return SamplingResult.drop();
            }
            return delegate.shouldSample(parentContext, traceId, name, spanKind, attributes, links);
        }

        @Override
        public String getDescription() {
            return "ConcurrentSpanFilter(delegate=" + delegate.getDescription() + ")";
        }
    }
}
