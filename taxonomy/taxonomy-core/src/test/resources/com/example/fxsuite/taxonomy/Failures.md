# Failures

The failures of a service, as a caller meets them.

## Failure

Something asked for was not done.

## Transient

Worth retrying: the same request may succeed later.

## Permanent

Not worth retrying: the same request fails the same way.

## Timeout

No answer came in time.

## RateLimited

Too many requests were made; fewer will be let through.

## NotFound

What was asked for is not there.

## Forbidden

The caller may not do this.
